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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.SampleNodeProvider;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.library.DictionaryBuilder;
import com.github.fge.jsonschema.processing.LogLevel;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Iterator;

import static com.github.fge.jsonschema.TestUtils.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class SyntaxProcessorTest
{
    private static final String K1 = "k1";

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

        final Dictionary<SyntaxChecker> dict
            = new DictionaryBuilder<SyntaxChecker>().build();

        final SyntaxProcessor processor = new SyntaxProcessor(dict);

        processor.process(report, data);

        verify(report).error(captor.capture());

        final JsonNode msgNode = captor.getValue().asJson();
        assertEquals(msgNode.get("message").textValue(),
            "document is not a JSON Schema: not an object");
    }

    @Test
    public void unknownKeywordsAreReportedAsWarnings()
        throws ProcessingException
    {
        final ObjectNode node = JacksonUtils.nodeFactory().objectNode();
        node.put(K1, K1);

        final ProcessingReport report = mock(ProcessingReport.class);
        final JsonSchemaTree tree = new CanonicalSchemaTree(node);
        final ValidationData data = new ValidationData(tree);

        final Dictionary<SyntaxChecker> dict
            = new DictionaryBuilder<SyntaxChecker>().build();

        final SyntaxProcessor processor = new SyntaxProcessor(dict);

        final ArrayNode ignored = JacksonUtils.nodeFactory().arrayNode();
        ignored.add(K1);

        final ArgumentCaptor<ProcessingMessage> captor
            = ArgumentCaptor.forClass(ProcessingMessage.class);

        processor.process(report, data);
        verify(report).log(captor.capture());

        final ProcessingMessage message = captor.getValue();
        assertEquals(message.getLogLevel(), LogLevel.WARNING);

        final JsonNode msgNode = message.asJson();
        assertEquals(msgNode.get("message").textValue(),
            "unknown keyword(s) found; ignored");
        assertEquals(msgNode.get("ignored"), ignored);
    }

    // This one is probably not needed, but...
    @Test
    public void equivalentSchemasAreNotCheckedTwice()
        throws ProcessingException
    {
        final ObjectNode node = JacksonUtils.nodeFactory().objectNode();
        node.put(K1, K1);

        final ProcessingReport report = mock(ProcessingReport.class);
        final JsonSchemaTree tree = new CanonicalSchemaTree(node);
        final ValidationData data = new ValidationData(tree);

        final SyntaxChecker checker = mock(SyntaxChecker.class);

        final Dictionary<SyntaxChecker> dict
            = new DictionaryBuilder<SyntaxChecker>().addEntry(K1, checker)
                .build();
        final SyntaxProcessor processor = new SyntaxProcessor(dict);

        processor.process(report, data);
        processor.process(report, data);

        verify(checker, onlyOnce()).checkSyntax(any(SyntaxProcessor.class),
            any(ProcessingReport.class), any(JsonSchemaTree.class));
    }

    @Test
    public void errorsAreCorrectlyReported()
        throws ProcessingException
    {
        final ProcessingMessage msg = new ProcessingMessage().msg("foo");

        final SyntaxChecker checker = new SyntaxChecker()
        {
            @Override
            public void checkSyntax(final SyntaxProcessor processor,
                final ProcessingReport report, final JsonSchemaTree tree)
                throws ProcessingException
            {
                report.error(msg);
            }
        };

        final Dictionary<SyntaxChecker> dict
            = new DictionaryBuilder<SyntaxChecker>().addEntry(K1, checker)
            .build();

        final SyntaxProcessor processor = new SyntaxProcessor(dict);

        final ObjectNode schema = JacksonUtils.nodeFactory().objectNode();
        schema.put(K1, "");

        final JsonSchemaTree tree = new CanonicalSchemaTree(schema);
        final ProcessingReport report = mock(ProcessingReport.class);

        final ValidationData data = new ValidationData(tree);

        processor.process(report, data);

        verify(report).log(msg);
        assertEquals(msg.getLogLevel(), LogLevel.ERROR);
    }

    @Test
    public void checkingWillNotDiveIntoUnknownKeywords()
        throws ProcessingException
    {
        final ObjectNode node = JacksonUtils.nodeFactory().objectNode();
        node.put(K1, K1);
        final ObjectNode schema = JacksonUtils.nodeFactory().objectNode();
        schema.put("foo", node);
        final JsonSchemaTree tree = new CanonicalSchemaTree(schema);
        final ValidationData data = new ValidationData(tree);

        final SyntaxChecker checker = mock(SyntaxChecker.class);

        final Dictionary<SyntaxChecker> dict
            = new DictionaryBuilder<SyntaxChecker>().addEntry(K1, checker)
            .build();
        final SyntaxProcessor processor = new SyntaxProcessor(dict);

        final ProcessingReport report = mock(ProcessingReport.class);
        processor.process(report, data);
        verify(checker, never()).checkSyntax(any(SyntaxProcessor.class),
            any(ProcessingReport.class), any(JsonSchemaTree.class));
    }
}
