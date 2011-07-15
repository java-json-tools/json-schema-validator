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
import eel.kitchen.util.NodeType;
import eel.kitchen.util.RhinoHelper;
import org.codehaus.jackson.JsonNode;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class ObjectValidator
    extends AbstractValidator
{
    private static final Map<String, EnumSet<NodeType>> FIELDS
        = new LinkedHashMap<String, EnumSet<NodeType>>();

    private final Collection<String> required = new HashSet<String>();
    private boolean additionalPropertiesOK = true;
    private JsonNode additionalProperties = EMPTY_SCHEMA;
    private final Map<String, Set<String>> dependencies
        = new HashMap<String, Set<String>>();
    private final Map<String, JsonNode> properties
        = new HashMap<String, JsonNode>();
    private final Map<String, JsonNode> patternProperties
        = new HashMap<String, JsonNode>();

    public ObjectValidator()
    {
        registerField("properties", NodeType.OBJECT);
        registerField("additionalProperties", NodeType.OBJECT);
        registerField("additionalProperties", NodeType.BOOLEAN);
        registerField("patternProperties", NodeType.OBJECT);
        registerField("dependencies", NodeType.OBJECT);
    }

    @Override
    protected Map<String, EnumSet<NodeType>> fieldMap()
    {
        return FIELDS;
    }

    @Override
    protected boolean doSetup()
    {
        return super.doSetup() && computeProperties() && computeAdditional()
            && computeDependencies() && computePatternProperties();

    }

    private boolean computeProperties()
    {
        final JsonNode node = schema.get("properties");

        if (node == null)
            return true;

        final Map<String, JsonNode> map = CollectionUtils.toMap(node.getFields());

        JsonNode value, truthValue;

        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            value = entry.getValue();
            if (!value.isObject()) {
                messages.add("values of properties should be objects");
                return false;
            }
            truthValue = value.get("required");
            if (truthValue == null)
                continue;
            if (!truthValue.isBoolean()) {
                messages.add("required should be a boolean");
                return false;
            }
            if (truthValue.getBooleanValue())
                required.add(entry.getKey());
        }

        properties.putAll(map);
        return true;
    }

    private boolean computeAdditional()
    {
        final JsonNode node = schema.path("additionalProperties");

        if (node.isBoolean())
            additionalPropertiesOK = node.getBooleanValue();

        if (node.isObject())
            additionalProperties = node;

        return true;
    }

    private boolean computeDependencies()
    {
        //TODO: object dependencies

        final JsonNode node = schema.path("dependencies");

        if (!node.isObject())
            return true;

        final Map<String, JsonNode> map = CollectionUtils.toMap(node.getFields());
        Set<String> deps;
        String fieldName;

        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            try {
                deps = computeOneDependency(entry.getValue());
            } catch (MalformedJasonSchemaException e) {
                messages.add(e.getMessage());
                return false;
            }
            fieldName = entry.getKey();
            if (deps.contains(fieldName)) {
                messages.add("a property cannot depend on itself");
                return false;
            }
            dependencies.put(fieldName, deps);
        }
        return true;
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
            throw new MalformedJasonSchemaException("dependency value should "
                + "be a string or an array");

        for (final JsonNode element: node) {
            if (!element.isTextual())
                throw new MalformedJasonSchemaException("dependency "
                    + "array elements should be strings");
            if (!ret.add(element.getTextValue()))
                throw new MalformedJasonSchemaException("duplicate entries "
                    + "in dependency array");
        }

        return ret;
    }

    private boolean computePatternProperties()
    {
        final JsonNode node = schema.get("patternProperties");

        if (node == null)
            return true;

        final Map<String, JsonNode> map = CollectionUtils.toMap(node.getFields());

        String regex;
        JsonNode value;

        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            regex = entry.getKey();
            value = entry.getValue();
            if (!RhinoHelper.regexIsValid(regex)) {
                messages.add("invalid regex found in patternProperties");
                return false;
            }
            if (!value.isObject()) {
                messages.add("values of patternProperties should be objects");
                return false;
            }
            patternProperties.put(regex, value);
        }
        return true;
    }

    @Override
    public boolean validate(final JsonNode node)
    {
        if (!setup())
            return false;

        messages.clear();
        final Set<String> fields = CollectionUtils.toSet(node.getFieldNames());
        final Collection<String> set = new HashSet<String>();

        set.addAll(required);
        set.removeAll(fields);

        if (!set.isEmpty())
            for (final String field: set)
                messages.add("property " + field + " is required "
                    + "but was not found");

        set.clear();
        set.addAll(dependencies.keySet());
        set.retainAll(fields);
        final Map<String, Set<String>> map
            = new HashMap<String, Set<String>>(set.size());

        for (final String dep: set)
            map.put(dep, dependencies.get(dep));

        for (final Map.Entry<String, Set<String>> entry: map.entrySet()) {
            final String field = entry.getKey();
            set.clear();
            set.addAll(entry.getValue());
            set.removeAll(fields);
            for (final String dep: set)
                messages.add("property " + field + " depends on " + dep
                    + ", but the latter was not found");
        }

        fields.removeAll(properties.keySet());

        set.clear();

        for (final String field: fields)
            for (final String regex: patternProperties.keySet())
                if (RhinoHelper.regMatch(regex, field)) {
                    set.add(field);
                    break;
                }

        fields.removeAll(set);

        if (additionalPropertiesOK || fields.isEmpty())
            return messages.isEmpty();

        messages.add("additional properties were found but schema forbids them");
        return false;
    }

    @Override
    public SchemaProvider getSchemaProvider()
    {
        return new ObjectSchemaProvider(properties, patternProperties,
            additionalProperties);
    }
}
