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

package com.github.fge.jsonschema.processors.refexpand;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.google.common.collect.Queues;

import java.util.Deque;

public final class MutableTree
{
    private final ObjectNode baseNode = JacksonUtils.nodeFactory().objectNode();

    private ObjectNode currentNode = baseNode;

    private final Deque<ObjectNode> nodeStack = Queues.newArrayDeque();

    private JsonPointer currentPointer = JsonPointer.empty();

    private final Deque<JsonPointer> pointerStack = Queues.newArrayDeque();

    public ObjectNode getBaseNode()
    {
        return baseNode;
    }

    public void pushd(final JsonPointer pointer)
    {
        nodeStack.push(currentNode);
        pointerStack.push(currentPointer);
        currentNode = (ObjectNode) pointer.get(currentNode);
        currentPointer = currentPointer.append(pointer);
    }

    public void pop()
    {
        currentNode = nodeStack.pop();
        currentPointer = pointerStack.pop();
    }

    public void setCurrentNode(final JsonNode node)
    {
        currentNode.removeAll();
        currentNode.putAll(JacksonUtils.asMap(node));
    }

    @Override
    public String toString()
    {
        return "pointer: " + currentPointer + "; node: " + baseNode;
    }
}
