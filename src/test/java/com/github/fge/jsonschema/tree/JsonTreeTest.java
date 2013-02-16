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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.util.JacksonUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public final class JsonTreeTest
{
    private final JsonNodeFactory factory = JacksonUtils.nodeFactory();
    private JsonNode testNode;
    private ObjectNode childObject;

    @BeforeClass
    public void init()
    {
        childObject = factory.objectNode();
        childObject.put("a", "b");

        final ObjectNode rootNode = factory.objectNode();
        rootNode.put("object", childObject);
        testNode = rootNode;
    }

    @Test
    public void initializedNodeTreeReturnsCorrectNodeAndPointer()
    {
        final JsonTree tree = new SimpleJsonTree(testNode);
        assertSame(tree.getNode(), testNode);
        assertEquals(tree.getPointer(), JsonPointer.empty());
    }

    @Test
    public void pushdOfJsonPointerWorks()
    {
        JsonTree tree = new SimpleJsonTree(testNode);
        final JsonPointer ptr = JsonPointer.of("object", "a");
        tree = tree.append(ptr);
        assertSame(tree.getNode(), childObject.get("a"));
        assertEquals(tree.getPointer(), ptr);
    }
}
