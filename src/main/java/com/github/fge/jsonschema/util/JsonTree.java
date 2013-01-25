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

package com.github.fge.jsonschema.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.google.common.collect.Queues;

import java.util.Deque;

public abstract class JsonTree
{
    protected final JsonNode baseNode;

    protected final Deque<JsonPointer> dirStack = Queues.newArrayDeque();
    protected final Deque<JsonNode> nodeStack = Queues.newArrayDeque();

    protected JsonPointer pwd = JsonPointer.empty();
    protected JsonNode currentNode;

    protected JsonTree(final JsonNode baseNode)
    {
        this.baseNode = currentNode = baseNode;
    }

    public void pushd(final String pathElement)
    {
        dirStack.push(pwd);
        pwd = pwd.append(pathElement);
        nodeStack.push(currentNode);
        currentNode = currentNode.path(pathElement);
    }

    public void pushd(final int index)
    {
        dirStack.push(pwd);
        pwd = pwd.append(index);
        nodeStack.push(currentNode);
        currentNode = currentNode.path(index);
    }

    public void pushd(final JsonPointer ptr)
    {
        dirStack.push(pwd);
        pwd = pwd.append(ptr);
        nodeStack.push(currentNode);
        currentNode = pwd.resolve(currentNode);
    }

    public void popd()
    {
        pwd = dirStack.pop();
        currentNode = nodeStack.pop();
    }

    public final JsonNode getCurrentNode()
    {
        return currentNode;
    }

    public final JsonPointer pwd()
    {
        return pwd;
    }

    @Override
    public String toString()
    {
        return "pwd = \"" + pwd + "\"; current = " + currentNode;
    }
}

