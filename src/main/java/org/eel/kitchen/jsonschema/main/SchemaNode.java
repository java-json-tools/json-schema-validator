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

package org.eel.kitchen.jsonschema.main;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * A schema node
 *
 * <p>This embodies the parent schema, as a {@link SchemaContainer},
 * and the schema node itself, as a {@link JsonNode}.</p>
 */
public final class SchemaNode
{
    private final SchemaContainer container;
    private final JsonNode node;

    public SchemaNode(final SchemaContainer container, final JsonNode node)
    {
        this.container = container;
        this.node = node;
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
}
