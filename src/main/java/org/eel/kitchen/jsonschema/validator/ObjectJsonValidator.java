/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableSet;
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.main.SchemaContainer;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.ref.JsonPointer;
import org.eel.kitchen.jsonschema.util.JacksonUtils;
import org.eel.kitchen.jsonschema.util.RhinoHelper;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Validator called for object instance children
 *
 * <p>Unlike what happens with arrays, a same child/value instance of an object
 * may have to satisfy more than one schema. For a given property name, the list
 * of schemas is constructed as follows:</p>
 *
 * <ul>
 *     <li>if the property name has an exact match in {@code properties},
 *     the corresponding schema is added to the list;</li>
 *     <li>for all regexes in {@code patternProperties}, if the property name
 *     matches the regex, the corresponding schema is added to the list;</li>
 *     <li>if, at this point, the list is empty, then the contents of
 *     {@code additionalProperties} is added to the list (an empty schema if
 *     {@code additionalProperties} is either {@code true} or nonexistent).</li>
 * </ul>
 *
 * <p>Its {@link #next()} method should <b>never</b> be called (it throws an
 * {@link IllegalStateException} if it is).</p>
 */
public final class ObjectJsonValidator
    extends JsonValidator
{
    private final JsonNode additionalProperties;
    private final Map<String, JsonNode> properties;
    private final Map<String, JsonNode> patternProperties;

    ObjectJsonValidator(final JsonSchemaFactory factory,
        final SchemaNode schemaNode)
    {
        super(factory, schemaNode);

        final JsonNode schema = schemaNode.getNode();

        JsonNode node;

        node = schema.path("additionalProperties");
        additionalProperties = node.isObject() ? node : EMPTY_SCHEMA;

        node = schema.path("properties");
        properties = node.isObject() ? JacksonUtils.nodeToMap(node)
            : Collections.<String, JsonNode>emptyMap();

        node = schema.path("patternProperties");
        patternProperties = node.isObject() ? JacksonUtils.nodeToMap(node)
            : Collections.<String, JsonNode>emptyMap();
    }

    @Override
    public boolean validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        final JsonPointer pwd = report.getPath();
        final Map<String, JsonNode> map = JacksonUtils.nodeToMap(instance);

        for (final Map.Entry<String, JsonNode> entry: map.entrySet())
            validateOne(context, report, entry);

        report.setPath(pwd);
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public JsonValidator next()
    {
        throw new IllegalStateException("I should not have been called");
    }

    private void validateOne(final ValidationContext context,
        final ValidationReport report, final Map.Entry<String, JsonNode> entry)
    {
        final String key = entry.getKey();
        final JsonNode value = entry.getValue();
        final SchemaContainer container = context.getContainer();
        final JsonPointer ptr = report.getPath().append(key);
        final Set<JsonNode> subSchemas = getSchemas(key);

        JsonValidator validator;
        SchemaNode subNode;

        report.setPath(ptr);

        for (final JsonNode subSchema: subSchemas) {
            subNode = new SchemaNode(container, subSchema);
            validator = new RefResolverJsonValidator(factory, subNode);
            while (validator.validate(context, report, value))
                validator = validator.next();
        }
    }

    private Set<JsonNode> getSchemas(final String key)
    {
        final Set<JsonNode> ret = new HashSet<JsonNode>();

        if (properties.containsKey(key))
            ret.add(properties.get(key));

        for (final String regex: patternProperties.keySet())
            if (RhinoHelper.regMatch(regex, key))
                ret.add(patternProperties.get(regex));

        if (ret.isEmpty())
            ret.add(additionalProperties);

        return ImmutableSet.copyOf(ret);
    }
}
