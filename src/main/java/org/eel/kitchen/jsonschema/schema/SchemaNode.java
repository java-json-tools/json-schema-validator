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

package org.eel.kitchen.jsonschema.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.util.ArrayList;
import java.util.List;

public final class SchemaNode
{
    private static final JsonNode EMPTY_SCHEMA
        = JsonNodeFactory.instance.objectNode();

    private final SchemaContainer container;
    private final JsonNode node;

    private JsonNode additionalItems = EMPTY_SCHEMA;

    private final List<JsonNode> items = new ArrayList<JsonNode>();

    public SchemaNode(final SchemaContainer container, final JsonNode node)
    {
        this.container = container;
        this.node = node;

        setupArraySchemas();
    }

    private void setupArraySchemas()
    {
        JsonNode tmp;

        tmp = node.path("items");

        if (tmp.isObject()) {
            additionalItems = tmp;
            return;
        }

        if (tmp.isArray())
            for (final JsonNode item: tmp)
                items.add(item);

        tmp = node.path("additionalItems");

        if (tmp.isObject())
            additionalItems = tmp;
    }

    public SchemaContainer getContainer()
    {
        return container;
    }

    public JsonNode getNode()
    {
        return node;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;

        final SchemaNode that = (SchemaNode) o;

        return container.equals(that.container)
            && node.equals(that.node);
    }

    @Override
    public int hashCode()
    {
        return 31 * container.hashCode() + node.hashCode();
    }

    public JsonNode getArraySchema(final int index)
    {
        return index < items.size() ? items.get(index) : additionalItems;
    }
}
