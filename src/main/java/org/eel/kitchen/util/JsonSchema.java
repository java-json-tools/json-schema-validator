/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.util;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class JsonSchema
{
    private static final JsonNode EMPTY_SCHEMA
        = JsonNodeFactory.instance.objectNode();

    private final JsonNode schema;

    private final List<JsonNode> items = new ArrayList<JsonNode>();

    private JsonNode additionalItems = EMPTY_SCHEMA;

    private final Map<String, JsonNode> properties
        = new HashMap<String, JsonNode>();

    private final Map<String, JsonNode> patternProperties
        = new HashMap<String, JsonNode>();

    private JsonNode additionalProperties = EMPTY_SCHEMA;

    public JsonSchema(final JsonNode schema)
    {
        this.schema = schema;
        setupArrayNodes();
        setupObjectNodes();
    }

    private void setupArrayNodes()
    {
        JsonNode node = schema.path("items");

        if (node.isObject()) {
            additionalItems = node;
            return;
        }

        if (node.isArray())
            for (final JsonNode item: node)
                items.add(item);

        node = schema.path("additionalItems");

        if (node.isObject())
            additionalItems = node;
    }

    private void setupObjectNodes()
    {
        JsonNode node = schema.path("properties");

        if (node.isObject())
            properties.putAll(CollectionUtils.toMap(node.getFields()));

        node = schema.path("patternProperties");

        if (node.isObject())
            patternProperties.putAll(CollectionUtils.toMap(node.getFields()));

        node = schema.path("additionalProperties");

        if (node.isObject())
            additionalProperties = node;
    }

    public JsonNode arrayPath(final int index)
    {
        return index < items.size() ? items.get(index) : additionalItems;
    }

    public Collection<JsonNode> objectPath(final String path)
    {
        final Set<JsonNode> ret = new HashSet<JsonNode>();

        if (properties.containsKey(path))
            ret.add(properties.get(path));

        for (final Map.Entry<String, JsonNode> entry:
            patternProperties.entrySet())
            if (RhinoHelper.regMatch(entry.getKey(), path))
                ret.add(entry.getValue());

        if (ret.isEmpty())
            ret.add(additionalProperties);

        return ret;
    }
}
