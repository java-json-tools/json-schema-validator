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

package com.github.fge.jsonschema.processing;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.main.JsonSchemaException;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.github.fge.jsonschema.tree.JsonTree;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class ProcessingMessageTest
{
    @Test
    public void treeContextIsReportedInMessages()
    {
        final JsonPointer ptr = JsonPointer.empty().append("foo").append("bar");
        final JsonTree tree = mock(JsonTree.class);
        when(tree.getCurrentPointer()).thenReturn(ptr);

        final ProcessingMessage msg = new ProcessingMessage(tree);
        final JsonNode node = msg.asJson().path("instancePointer");

        assertTrue(node.isTextual());
        assertEquals(node.textValue(), ptr.toString());
    }

    @Test
    public void schemaContextIsReportedInMessages()
        throws JsonSchemaException
    {
        final JsonRef loadingRef = JsonRef.fromString("zoo://my.zoo/x.json");
        final JsonRef ctx = JsonRef.fromString("meh://I/am/there#/a");
        final JsonPointer schemaPointer = new JsonPointer("/a/b/c/");
        final JsonPointer instancePointer = JsonPointer.empty();

        final JsonTree tree = mock(JsonTree.class);
        when(tree.getCurrentPointer()).thenReturn(instancePointer);

        final JsonSchemaTree schemaTree = mock(JsonSchemaTree.class);
        when(schemaTree.getLoadingRef()).thenReturn(loadingRef);
        when(schemaTree.getCurrentRef()).thenReturn(ctx);
        when(schemaTree.getCurrentPointer()).thenReturn(schemaPointer);

        final ProcessingMessage msg = new ProcessingMessage(schemaTree, tree);

        final JsonNode msgNode = msg.asJson();
        final JsonNode instanceNode = msgNode.path("instancePointer");
        final JsonNode schemaInfo = msgNode.path("schema");

        assertTrue(instanceNode.isTextual());
        assertEquals(instanceNode.textValue(), instancePointer.toString());

        assertTrue(schemaInfo.isObject());

        JsonNode node;

        node = schemaInfo.path("location");
        assertTrue(node.isTextual());
        assertEquals(node.textValue(), loadingRef.toString());

        node = schemaInfo.path("pointer");
        assertTrue(node.isTextual());
        assertEquals(node.textValue(), schemaPointer.toString());

        node = schemaInfo.path("uriContext");
        assertTrue(node.isTextual());
        assertEquals(node.textValue(), ctx.toString());
    }
}
