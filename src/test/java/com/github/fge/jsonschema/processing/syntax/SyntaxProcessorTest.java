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

package com.github.fge.jsonschema.processing.syntax;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.SampleNodeProvider;
import com.github.fge.jsonschema.main.JsonSchemaException;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.InlineSchemaTree;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import com.google.common.collect.Maps;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.Map;

import static com.github.fge.jsonschema.processing.syntax.SyntaxProcessor.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class SyntaxProcessorTest
{
    private static final String K1 = "k1";
    private static final String K2 = "k2";

    @Test
    public void treesWithTheSameContentAndPointerAreEquivalent()
        throws JsonSchemaException
    {
        final JsonNode schema = JacksonUtils.emptyObject();
        final JsonRef ref = JsonRef.fromString("foo://bar");

        final JsonSchemaTree canonical1 = new CanonicalSchemaTree(ref, schema);
        final JsonSchemaTree canonical2 = new CanonicalSchemaTree(schema);
        final JsonSchemaTree inline1 = new InlineSchemaTree(ref, schema);
        final JsonSchemaTree inline2 = new InlineSchemaTree(schema);

        assertTrue(EQUIVALENCE.equivalent(canonical1, canonical2));
        assertTrue(EQUIVALENCE.equivalent(canonical2, inline1));
        assertTrue(EQUIVALENCE.equivalent(inline1, inline2));
        assertTrue(EQUIVALENCE.equivalent(inline2, canonical1));
    }

    @Test
    public void treesWithTheSameContentAndDifferentPointersAreNotEquivalent()
    {
        final JsonNode schema = JacksonUtils.emptyObject();

        final JsonSchemaTree tree1 = new CanonicalSchemaTree(schema);
        final JsonSchemaTree tree2 = tree1.copy();
        tree2.setPointer(JsonPointer.empty().append("foobar"));

        assertFalse(EQUIVALENCE.equivalent(tree1, tree2));
    }

    @DataProvider
    public Iterator<Object[]> notSchemas()
    {
        return SampleNodeProvider.getSamplesExcept(NodeType.OBJECT);
    }

    @Test(dataProvider = "notSchemas")
    public void syntaxProcessorYellsOnNonSchemas(final JsonNode node)
        throws ProcessingException
    {
        final ArgumentCaptor<ProcessingMessage> captor
            = ArgumentCaptor.forClass(ProcessingMessage.class);

        final ProcessingReport report = mock(ProcessingReport.class);
        final JsonSchemaTree tree = new CanonicalSchemaTree(node);
        final ValidationData data = new ValidationData(tree);

        final Map<String, SyntaxChecker> map = Maps.newHashMap();
        final SyntaxProcessor processor = new SyntaxProcessor(map);

        processor.process(report, data);

        verify(report).error(captor.capture());

        final JsonNode msgNode = captor.getValue().asJson();
        assertEquals(msgNode.get("message").textValue(),
            "document is not a JSON Schema: not an object");
    }
}
