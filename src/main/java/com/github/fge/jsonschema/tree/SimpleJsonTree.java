/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.jsonschema.tree;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;

/**
 * A simple {@link JsonTree}
 */
public final class SimpleJsonTree
    extends BaseJsonTree
{
    public SimpleJsonTree(final JsonNode baseNode)
    {
        super(baseNode);
    }

    private SimpleJsonTree(final JsonNode baseNode, final JsonPointer pointer)
    {
        super(baseNode, pointer);
    }

    @Override
    public SimpleJsonTree append(final JsonPointer pointer)
    {
        return new SimpleJsonTree(baseNode, this.pointer.append(pointer));
    }

    @Override
    public JsonNode asJson()
    {
        return FACTORY.objectNode()
            .set("pointer", FACTORY.textNode(pointer.toString()));
    }

    @Override
    public String toString()
    {
        return "current pointer: \"" + pointer;
    }
}
