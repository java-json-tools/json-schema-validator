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

import eel.kitchen.util.CollectionUtils;
import eel.kitchen.util.RhinoHelper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;

import java.util.HashMap;
import java.util.Map;

final class ObjectPathProvider
    implements PathProvider
{
    private static final JsonNode EMPTY_SCHEMA
        = JsonNodeFactory.instance.objectNode();

    private static final Map<String, JsonNode> properties
        = new HashMap<String, JsonNode>();

    private static final Map<String, JsonNode> patternProperties
        = new HashMap<String, JsonNode>();

    private final JsonNode additionalProperties;

    ObjectPathProvider(final JsonNode schema)
    {
        JsonNode node = schema.path("properties");

        if (node.isObject())
            properties.putAll(CollectionUtils.toMap(node.getFields()));

        node = schema.path("patternProperties");

        if (node.isObject())
            patternProperties.putAll(CollectionUtils.toMap(node.getFields()));

        node = schema.path("additionalProperties");

        additionalProperties = node.isObject() ? node : EMPTY_SCHEMA;
    }

    @Override
    public JsonNode getSchema(final String path)
    {
        if (properties.containsKey(path))
            return properties.get(path);

        for (final String pattern: patternProperties.keySet())
            if (RhinoHelper.regMatch(pattern, path))
                return patternProperties.get(pattern);

        return additionalProperties;
    }
}
