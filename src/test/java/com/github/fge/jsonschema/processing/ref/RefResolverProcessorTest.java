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

package com.github.fge.jsonschema.processing.ref;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class RefResolverProcessorTest
{
    private final Processor<ValidationData, ValidationData> processor
        = new RefResolverProcessor(null);
    private final ProcessingReport report = mock(ProcessingReport.class);

    @Test
    public void refLoopsAreReported()
    {
        final ObjectNode node = JacksonUtils.nodeFactory().objectNode();
        node.put("$ref", "#");

        final JsonSchemaTree tree = new CanonicalSchemaTree(node);

        final ValidationData data = new ValidationData(tree, null);

        try {
            processor.process(report, data);
            fail("No exception thrown!");
        } catch (ProcessingException e) {
            final JsonNode msg = e.getProcessingMessage().asJson();
            assertEquals(msg.get("message").textValue(),
                "JSON Reference loop detected");
        }
    }

    @Test
    public void danglingRefsAreReported()
    {
        final ObjectNode node = JacksonUtils.nodeFactory().objectNode();
        node.put("$ref", "#/a");

        final JsonSchemaTree tree = new CanonicalSchemaTree(node);

        final ValidationData data = new ValidationData(tree, null);

        try {
            processor.process(report, data);
            fail("No exception thrown!");
        } catch (ProcessingException e) {
            final JsonNode msg = e.getProcessingMessage().asJson();
            assertEquals(msg.get("message").textValue(),
                "unresolvable JSON Reference");
        }
    }
}
