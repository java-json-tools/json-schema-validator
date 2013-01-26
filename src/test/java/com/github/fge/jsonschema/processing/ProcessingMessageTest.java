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
import com.github.fge.jsonschema.ref.JsonPointer;
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

        verify(tree).getCurrentPointer();
        assertTrue(node.isTextual());
        assertEquals(node.textValue(), ptr.toString());
    }
}
