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
import com.github.fge.jsonschema.ref.JsonPointer;
import com.google.common.collect.Queues;

import java.util.Deque;

/**
 * Base implementation of a {@link JsonTree}
 */
public abstract class BaseJsonTree
    implements JsonTree
{
    /**
     * The initial node
     */
    protected final JsonNode baseNode;

    /**
     * The queue of JSON Pointers
     */
    protected final Deque<JsonPointer> dirStack = Queues.newArrayDeque();

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

    @Override
    public void append(final String refToken)
    {
        dirStack.push(currentPointer);
        currentPointer = currentPointer.append(refToken);
        nodeStack.push(currentNode);
        currentNode = currentNode.path(refToken);
    }

    @Override
    public void append(final int index)
    {
        dirStack.push(currentPointer);
        currentPointer = currentPointer.append(index);
        nodeStack.push(currentNode);
        currentNode = currentNode.path(index);
    }

    @Override
    public void append(final JsonPointer ptr)
    {
        dirStack.push(currentPointer);
        currentPointer = currentPointer.append(ptr);
        nodeStack.push(currentNode);
        currentNode = currentPointer.resolve(currentNode);
    }

    @Override
    public void pop()
    {
        currentPointer = dirStack.pop();
        currentNode = nodeStack.pop();
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

    @Override
    public String toString()
    {
        return "current pointer: \"" + currentPointer
            + "\"; current node: " + currentNode;
    }
}

