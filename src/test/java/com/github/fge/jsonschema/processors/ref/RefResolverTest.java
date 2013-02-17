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

package com.github.fge.jsonschema.processors.ref;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.processors.data.SchemaHolder;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.JacksonUtils;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static com.github.fge.jsonschema.messages.RefProcessingMessages.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class RefResolverTest
{
    private final RefResolver processor = new RefResolver(null);
    private final ProcessingReport report = mock(ProcessingReport.class);

    @Test
    public void refLoopsAreReported()
    {
        final ObjectNode node = JacksonUtils.nodeFactory().objectNode();
        node.put("$ref", "#");

        final SchemaTree tree = new CanonicalSchemaTree(node);

        final SchemaHolder holder = new SchemaHolder(tree);

        try {
            processor.process(report, holder);
            fail("No exception thrown!");
        } catch (ProcessingException e) {
            assertMessage(e.getProcessingMessage()).hasMessage(REF_LOOP);
        }
    }

    @Test
    public void danglingRefsAreReported()
    {
        final ObjectNode node = JacksonUtils.nodeFactory().objectNode();
        node.put("$ref", "#/a");

        final SchemaTree tree = new CanonicalSchemaTree(node);

        final SchemaHolder holder = new SchemaHolder(tree);

        try {
            processor.process(report, holder);
            fail("No exception thrown!");
        } catch (ProcessingException e) {
            assertMessage(e.getProcessingMessage()).hasMessage(DANGLING_REF);
        }
    }
}
