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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.util.JacksonUtils;

/**
 * Base implementation of a {@link JsonTree}
 */
public abstract class BaseJsonTree
    implements JsonTree
{
    protected static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

    /**
     * The initial node
     */
    protected final JsonNode baseNode;

    /**
     * The current JSON Pointer into the node. Starts empty.
     */
    protected final JsonPointer pointer;

    /**
     * The current node.
     */
    protected final JsonNode node;

    /**
     * Protected constructor
     *
     * <p>This is equivalent to calling {@link
     * BaseJsonTree#BaseJsonTree(JsonNode, JsonPointer)} with an empty
     * pointer.</p>
     *
     * @param baseNode the base node
     */
    protected BaseJsonTree(final JsonNode baseNode)
    {
        this(baseNode, JsonPointer.empty());
    }

    /**
     * Main constructor
     *
     * @param baseNode the base node
     * @param pointer the pointer into the base node
     */
    protected BaseJsonTree(final JsonNode baseNode, final JsonPointer pointer)
    {
        this.baseNode = baseNode;
        node = pointer.resolve(baseNode);
        this.pointer = pointer;
    }

    @Override
    public final JsonNode getBaseNode()
    {
        return baseNode;
    }

    @Override
    public final JsonPointer getPointer()
    {
        return pointer;
    }

    @Override
    public final JsonNode getNode()
    {
        return node;
    }

    @Override
    public abstract String toString();
}

