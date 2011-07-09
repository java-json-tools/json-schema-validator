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

package eel.kitchen.jsonschema.validators;

import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import eel.kitchen.util.CollectionUtils;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.codehaus.jackson.JsonNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
    private final Map<Pattern, JsonNode> patternProperties
        = new HashMap<Pattern, JsonNode>();

    public ObjectValidator(final JsonNode schemaNode)
    {
        super(schemaNode);
    }

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
        final JsonNode node = schemaNode.get("properties");

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
        final JsonNode node = schemaNode.get("additionalProperties");

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

        final JsonNode node = schemaNode.get("dependencies");

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
        final JsonNode node = schemaNode.get("patternProperties");

        if (node == null)
            return;

        if (!node.isObject())
            throw new MalformedJasonSchemaException("patternProperties should" +
                " be an object");

        final Map<String, JsonNode> map = CollectionUtils.toMap(node.getFields());

        final Perl5Compiler compiler = new Perl5Compiler();
        String regex;
        Pattern pattern;
        JsonNode value;

        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            regex = entry.getKey();
            value = entry.getValue();
            try {
                pattern = compiler.compile(regex);
            } catch (MalformedPatternException e) {
                throw new MalformedJasonSchemaException("invalid regex found " +
                    "in patternProperties", e);
            }
            if (!value.isObject())
                throw new MalformedJasonSchemaException("values from " +
                    "patternProperties should be objects");
            patternProperties.put(pattern, value);
        }
    }

    @Override
    public boolean validate(final JsonNode node)
    {
        final Set<String> fields = CollectionUtils.toSet(node.getFieldNames());

        /*
         * Required
         */
        for (final String field: required)
            if (!fields.contains(field)) {
                validationErrors.add("property " + field + " is required "
                    + "but was not found");
            }

        for (final Map.Entry<String, Set<String>> entry: dependencies.entrySet()) {
            final String field = entry.getKey();
            final Set<String> deps = entry.getValue();
            if (!fields.contains(field))
                continue;
            for (final String dep: deps) {
                if (fields.contains(dep))
                    continue;
                validationErrors.add("property " + field + " depends on " + dep
                    + " but the latter was not found");
            }
        }

        fields.removeAll(properties.keySet());

        final PatternMatcher matcher = new Perl5Matcher();
        final Set<String> matches = new HashSet<String>();

        for (final String field: fields)
            for (final Pattern pattern: patternProperties.keySet())
                if (matcher.matches(field, pattern)) {
                    matches.add(field);
                    break;
                }

        fields.removeAll(matches);

        if (additionalPropertiesOK || fields.isEmpty())
            return validationErrors.isEmpty();

        validationErrors.add("additional properties were found but schema "
            + "forbids them");
        return false;
    }

    @Override
    public List<JsonNode> getSchemasForPath(final String subPath)
    {

        if (properties.containsKey(subPath))
            return Arrays.asList(properties.get(subPath));

        final PatternMatcher matcher = new Perl5Matcher();
        final List<JsonNode> ret = new ArrayList<JsonNode>();

        for (final Map.Entry<Pattern, JsonNode> entry: patternProperties.entrySet())
            if (matcher.contains(subPath, entry.getKey()))
                ret.add(entry.getValue());

        if (!ret.isEmpty())
            return Collections.unmodifiableList(ret);

        return Arrays.asList(additionalProperties);
    }
}
