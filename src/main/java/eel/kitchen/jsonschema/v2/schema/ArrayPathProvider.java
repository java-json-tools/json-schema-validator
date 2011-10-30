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

package eel.kitchen.jsonschema.v2.schema;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;

import java.util.LinkedList;
import java.util.List;

final class ArrayPathProvider
    implements PathProvider
{
    private static final JsonNode EMPTY_SCHEMA
        = JsonNodeFactory.instance.objectNode();

    private final List<JsonNode> items = new LinkedList<JsonNode>();

    private final JsonNode additionalItems;

    ArrayPathProvider(final JsonNode schema)
    {
        JsonNode node = schema.path("items");

        if (node.isObject()) {
            additionalItems = node;
            return;
        }

        if (node.isArray()) {
            for (final JsonNode item: node)
                items.add(item);
        }

        node = schema.path("additionalItems");

        additionalItems = node.isObject() ? node : EMPTY_SCHEMA;
    }

    @Override
    public JsonNode getSchema(final String path)
    {
        final int index;
        try {
            index = Integer.parseInt(path);
            if (index < 0)
                throw new NumberFormatException("index is negative");
        } catch (NumberFormatException e) {
            throw new RuntimeException("Tried to access schema for array "
                + "instance element with an illegal index (" + path + ")", e);
        }

        try {
            return items.get(index);
        } catch (IndexOutOfBoundsException ignored) {
            return additionalItems;
        }
    }
}
