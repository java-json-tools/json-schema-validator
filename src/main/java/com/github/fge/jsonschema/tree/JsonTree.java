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
import com.fasterxml.jackson.databind.node.MissingNode;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.google.common.collect.Queues;

import java.util.Deque;

/**
 * A JSON value decorated with JSON Pointer information
 *
 * <p>This is a {@link JsonNode} wrapped with pointer information as a {@link
 * JsonPointer}. The available methods allow to append individual reference
 * tokens and retrieve the node at the current pointer. The {@code pop()}
 * operation reverts the last append.</p>
 *
 * <p>You can append individual strings, array indices or full JSON Pointers.
 * </p>
 *
 * <p>If the current pointer leads to a non existent value in the JSON value,
 * the current node is a {@link MissingNode}.</p>
 *
 * <p>Finally, remember that <b>JSON Pointers are always absolute,</b> which
 * means that <b>appending {@code ..} over {@code /a/b} will not lead to {@code
 * /a}, but {@code /a/b/..}</b>. {@code ..} is a perfectly valid member name for
 * a JSON Object!</p>
 *
 * @see JsonPointer
 */
public abstract class JsonTree
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
    protected JsonPointer pwd = JsonPointer.empty();

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
    protected JsonTree(final JsonNode baseNode)
    {
        this.baseNode = currentNode = baseNode;
    }

    /**
     * Append a reference token to the current path
     *
     * @param refToken the reference token
     * @see JsonPointer#append(String)
     */
    public void append(final String refToken)
    {
        dirStack.push(pwd);
        pwd = pwd.append(refToken);
        nodeStack.push(currentNode);
        currentNode = currentNode.path(refToken);
    }

    /**
     * Append an array index to the current path
     *
     * @param index the index
     * @see JsonPointer#append(int)
     */
    public void append(final int index)
    {
        dirStack.push(pwd);
        pwd = pwd.append(index);
        nodeStack.push(currentNode);
        currentNode = currentNode.path(index);
    }

    /**
     * Append a JSON Pointer to the current pointer
     *
     * @param ptr the pointer to append
     * @see JsonPointer#append(JsonPointer)
     */
    public void append(final JsonPointer ptr)
    {
        dirStack.push(pwd);
        pwd = pwd.append(ptr);
        nodeStack.push(currentNode);
        currentNode = pwd.resolve(currentNode);
    }

    /**
     * Reverts the last append
     *
     * <p>Note: this operation will fail badly if you haven't appended any token
     * yet, so use with care!</p>
     */
    public void pop()
    {
        pwd = dirStack.pop();
        currentNode = nodeStack.pop();
    }

    /**
     * Get the node at the current pointer
     *
     * @return the matching node (a {@link MissingNode} if there is no matching
     * node at that pointer)
     */
    public final JsonNode getCurrentNode()
    {
        return currentNode;
    }

    @Override
    public String toString()
    {
        return "pwd = \"" + pwd + "\"; current = " + currentNode;
    }
}

