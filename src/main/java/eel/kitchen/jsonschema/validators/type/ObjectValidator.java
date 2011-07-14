/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.validators.type;

import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import eel.kitchen.jsonschema.validators.AbstractValidator;
import eel.kitchen.jsonschema.validators.ObjectSchemaProvider;
import eel.kitchen.jsonschema.validators.SchemaProvider;
import eel.kitchen.util.CollectionUtils;
import eel.kitchen.util.RhinoHelper;
import org.codehaus.jackson.JsonNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ObjectValidator
    extends AbstractValidator
{
    private final Collection<String> required = new HashSet<String>();
    private boolean additionalPropertiesOK = true;
    private JsonNode additionalProperties = EMPTY_SCHEMA;
    private final Map<String, Set<String>> dependencies
        = new HashMap<String, Set<String>>();
    private final Map<String, JsonNode> properties
        = new HashMap<String, JsonNode>();
    private final Map<String, JsonNode> patternProperties
        = new HashMap<String, JsonNode>();

    @Override
    public void setup()
        throws MalformedJasonSchemaException
    {
        computeProperties();
        computeAdditional();
        computeDependencies();
        computePatternProperties();
    }

    private void computeProperties()
        throws MalformedJasonSchemaException
    {
        final JsonNode node = schema.get("properties");

        if (node == null)
            return;

        if (!node.isObject())
            throw new MalformedJasonSchemaException("properties is not an " +
                "object");

        final Map<String, JsonNode> map = CollectionUtils.toMap(node.getFields());

        JsonNode value, truthValue;

        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            value = entry.getValue();
            if (!value.isObject())
                throw new MalformedJasonSchemaException("value of a property " +
                    "should be an object");
            truthValue = value.get("required");
            if (truthValue == null)
                continue;
            if (!truthValue.isBoolean())
                throw new MalformedJasonSchemaException("required should be " +
                    "a boolean");
            if (truthValue.getBooleanValue())
                required.add(entry.getKey());
        }

        properties.putAll(map);
    }

    private void computeAdditional()
        throws MalformedJasonSchemaException
    {
        final JsonNode node = schema.get("additionalProperties");

        if (node == null)
            return;

        if (node.isBoolean()) {
            additionalPropertiesOK = node.getBooleanValue();
            return;
        }

        if (!node.isObject())
            throw new MalformedJasonSchemaException("additionalProperties" +
                " is neither a boolean nor an object");

        additionalProperties = node;
    }

    private void computeDependencies()
        throws MalformedJasonSchemaException
    {
        //TODO: object dependencies

        final JsonNode node = schema.get("dependencies");

        if (node == null)
            return;

        if (!node.isObject())
            throw new MalformedJasonSchemaException("dependencies should be "
                + "an object");

        final Map<String, JsonNode> map = CollectionUtils.toMap(node.getFields());
        Set<String> deps;
        String fieldName;

        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            deps = computeOneDependency(entry.getValue());
            fieldName = entry.getKey();
            if (deps.contains(fieldName))
                throw new MalformedJasonSchemaException("a property cannot " +
                    "depend on itself");
            dependencies.put(fieldName, deps);
        }
    }

    private static Set<String> computeOneDependency(final JsonNode node)
        throws MalformedJasonSchemaException
    {
        final Set<String> ret = new HashSet<String>();

        if (node.isTextual()) {
            ret.add(node.getTextValue());
            return ret;
        }

        if (!node.isArray())
            throw new MalformedJasonSchemaException("dependency value is "
                + "neither a string nor an array");

        for (final JsonNode element: node) {
            if (!element.isTextual())
                throw new MalformedJasonSchemaException("dependency element "
                    + "is not a string");
            if (!ret.add(element.getTextValue()))
                throw new MalformedJasonSchemaException("duplicate entries "
                    + "in dependencies array");
        }

        return ret;
    }

    private void computePatternProperties()
        throws MalformedJasonSchemaException
    {
        final JsonNode node = schema.get("patternProperties");

        if (node == null)
            return;

        if (!node.isObject())
            throw new MalformedJasonSchemaException("patternProperties should" +
                " be an object");

        final Map<String, JsonNode> map = CollectionUtils.toMap(node.getFields());

        String regex;
        JsonNode value;

        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            regex = entry.getKey();
            value = entry.getValue();
            if (!RhinoHelper.regexIsValid(regex))
                throw new MalformedJasonSchemaException("invalid regex found " +
                    "in patternProperties");
            if (!value.isObject())
                throw new MalformedJasonSchemaException("values from " +
                    "patternProperties should be objects");
            patternProperties.put(regex, value);
        }
    }

    @Override
    public boolean validate(final JsonNode node)
    {
        messages.clear();
        final Set<String> fields = CollectionUtils.toSet(node.getFieldNames());

        /*
         * Required
         */
        for (final String field: required)
            if (!fields.contains(field))
                messages.add("property " + field + " is required "
                    + "but was not found");

        for (final Map.Entry<String, Set<String>> entry: dependencies.entrySet()) {
            final String field = entry.getKey();
            final Set<String> deps = entry.getValue();
            if (!fields.contains(field))
                continue;
            for (final String dep: deps) {
                if (fields.contains(dep))
                    continue;
                messages.add("property " + field + " depends on " + dep
                    + ", but the latter was not found");
            }
        }

        fields.removeAll(properties.keySet());

        final Set<String> matches = new HashSet<String>();

        for (final String field: fields)
            for (final String regex: patternProperties.keySet())
                if (RhinoHelper.regMatch(regex, field)) {
                    matches.add(field);
                    break;
                }

        fields.removeAll(matches);

        if (additionalPropertiesOK || fields.isEmpty())
            return messages.isEmpty();

        messages.add("additional properties were found but schema "
            + "forbids them");
        return false;
    }

    @Override
    public SchemaProvider getSchemaProvider()
    {
        return new ObjectSchemaProvider(properties, patternProperties,
            additionalProperties);
    }
}
