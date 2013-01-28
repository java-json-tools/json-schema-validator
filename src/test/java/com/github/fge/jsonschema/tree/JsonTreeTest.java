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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public final class JsonTreeTest
{
    private final JsonNodeFactory factory = JacksonUtils.nodeFactory();
    private JsonNode testNode;
    private ObjectNode childObject;
    private ArrayNode childArray;

    @BeforeClass
    public void init()
    {
        childObject = factory.objectNode();
        childObject.put("a", "b");

        childArray = factory.arrayNode();
        childArray.add("c");

        final ObjectNode rootNode = factory.objectNode();
        rootNode.put("object", childObject);
        rootNode.put("array", childArray);
        testNode = rootNode;
    }

    @Test
    public void initializedNodeTreeReturnsCorrectNode()
    {
        final JsonTree tree = new SimpleJsonTree(testNode);
        assertSame(tree.getCurrentNode(), testNode);
    }

    @Test
    public void pushdOfJsonPointerWorks()
    {
        final JsonTree tree = new SimpleJsonTree(testNode);
        final JsonPointer ptr = JsonPointer.empty().append("object")
            .append("a");
        tree.append(ptr);
        assertSame(tree.getCurrentNode(), childObject.get("a"));
    }

    @Test(dependsOnMethods = "pushdOfJsonPointerWorks")
    public void popdWorks()
    {
        final JsonTree tree = new SimpleJsonTree(testNode);
        tree.append(JsonPointer.empty().append("object"));
        tree.append(JsonPointer.empty().append("a"));
        tree.pop();
        assertSame(tree.getCurrentNode(), childObject);
        tree.pop();
        assertSame(tree.getCurrentNode(), testNode);
    }
}
