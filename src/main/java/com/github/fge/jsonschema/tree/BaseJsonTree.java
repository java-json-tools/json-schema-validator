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
import com.google.common.collect.Queues;

import java.util.Deque;

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
     * The queue of JSON Pointers
     */
    protected final Deque<JsonPointer> pointerStack = Queues.newArrayDeque();

    /**
     * The queue of nodes
     */
    protected final Deque<JsonNode> nodeStack = Queues.newArrayDeque();

    /**
     * The current JSON Pointer into the node. Starts empty.
     */
    protected JsonPointer currentPointer = JsonPointer.empty();

    /**
     * The current node.
     */
    protected JsonNode currentNode;

    /**
     * Protected constructor
     *
     * <p>A newly constructed tree start at the root of the document.</p>
     *
     * @param baseNode the base node
     */
    protected BaseJsonTree(final JsonNode baseNode)
    {
        this.baseNode = currentNode = baseNode;
    }

    /**
     * Return the node this tree was created with
     * <p>Note: in current Jackson versions, this node is unfortunately mutable,
     * so be careful...</p>
     *
     * @return the node
     */
    @Override
    public final JsonNode getBaseNode()
    {
        return baseNode;
    }

    @Override
    public final JsonPointer getCurrentPointer()
    {
        return currentPointer;
    }

    @Override
    public final JsonNode getCurrentNode()
    {
        return currentNode;
    }

    protected final void pushPointer(final JsonPointer pointer)
    {
        pointerStack.push(currentPointer);
        currentPointer = pointer;
    }

    protected final void pushNode(final JsonNode node)
    {
        nodeStack.push(currentNode);
        currentNode = node;
    }

    protected final void popPointer()
    {
        currentPointer = pointerStack.pop();
    }

    protected final void popNode()
    {
        currentNode = nodeStack.pop();
    }

    @Override
    public abstract String toString();
}

