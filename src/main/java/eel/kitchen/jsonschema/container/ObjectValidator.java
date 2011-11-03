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

package eel.kitchen.jsonschema.container;

import eel.kitchen.jsonschema.context.ValidationContext;
import eel.kitchen.jsonschema.keyword.KeywordValidatorFactory;
import eel.kitchen.jsonschema.base.Validator;
import eel.kitchen.util.CollectionUtils;
import eel.kitchen.util.NodeType;
import eel.kitchen.util.RhinoHelper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

public final class ObjectValidator
    extends ContainerValidator
{
    private Map<String, JsonNode> properties;

    private Map<String, JsonNode> patternProperties;

    private JsonNode additionalProperties;

    public ObjectValidator(final Validator validator,
        final ValidationContext context, final JsonNode instance)
    {
        super(validator, context, instance);
    }

    @Override
    protected void buildPathProvider()
    {
        JsonNode node;

        properties = new HashMap<String, JsonNode>();
        node = schema.path("properties");

        if (node.isObject())
            properties.putAll(CollectionUtils.toMap(node.getFields()));

        patternProperties = new HashMap<String, JsonNode>();
        node = schema.path("patternProperties");

        if (node.isObject())
            patternProperties.putAll(CollectionUtils.toMap(node.getFields()));

        node = schema.path("additionalProperties");

        additionalProperties = node.isObject() ? node : EMPTY_SCHEMA;
    }

    @Override
    protected JsonNode getSchema(final String path)
    {
        if (properties.containsKey(path))
            return properties.get(path);

        for (final String pattern: patternProperties.keySet())
            if (RhinoHelper.regMatch(pattern, path))
                return patternProperties.get(pattern);

        return additionalProperties;
    }

    @Override
    protected void buildQueue()
    {
        final KeywordValidatorFactory factory = context.getKeywordFactory();

        final SortedMap<String, JsonNode> map
            = CollectionUtils.toSortedMap(instance.getFields());

        String fieldName;
        JsonNode schemaNode;
        ValidationContext ctx;
        Validator v;

        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            fieldName = entry.getKey();
            schemaNode = getSchema(fieldName);
            ctx = context.createContext(fieldName, schemaNode);
            v = factory.getValidator(ctx, entry.getValue());
            queue.add(v);
        }

    }
}
