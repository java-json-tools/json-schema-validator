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

package org.eel.kitchen.jsonschema.container;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;

import java.util.ArrayList;
import java.util.List;

public final class ArraySchemaNode
{
    private static final JsonNode EMPTY_SCHEMA
        = JsonNodeFactory.instance.objectNode();

    private final List<JsonNode> items = new ArrayList<JsonNode>();

    private JsonNode additionalItems = EMPTY_SCHEMA;

    public ArraySchemaNode(final JsonNode schema)
    {
        setupArrayNodes(schema);
    }

    private void setupArrayNodes(final JsonNode schema)
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

    public JsonNode arrayPath(final int index)
    {
        return index < items.size() ? items.get(index) : additionalItems;
    }
}
