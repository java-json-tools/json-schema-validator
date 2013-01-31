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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.SampleNodeProvider;
import com.github.fge.jsonschema.library.DictionaryBuilder;
import com.github.fge.jsonschema.processing.LogLevel;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.syntax.SyntaxChecker;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Iterator;

import static com.github.fge.jsonschema.TestUtils.*;
import static com.github.fge.jsonschema.syntax.SyntaxMessages.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class SyntaxProcessorTest
{
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();
    private static final String K1 = "k1";
    private static final String K2 = "k2";
    private static final String ERRMSG = "foo";

    private ProcessingReport report;
    private SyntaxProcessor processor;
    private SyntaxChecker checker;

    @BeforeMethod
    public void initialize()
    {
        report = mock(ProcessingReport.class);
        final DictionaryBuilder<SyntaxChecker> builder
            = new DictionaryBuilder<SyntaxChecker>();

        checker = mock(SyntaxChecker.class);
        builder.addEntry(K1, checker);
        builder.addEntry(K2, new SyntaxChecker()
        {
            @Override
            public void checkSyntax(final SyntaxProcessor processor,
                final ProcessingReport report, final JsonSchemaTree tree)
                throws ProcessingException
            {
                report.error(new ProcessingMessage().msg(ERRMSG));
            }
        });

        processor = new SyntaxProcessor(builder.build());
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

        final ValidationData data = schemaToData(node);

        processor.process(report, data);

        verify(report).error(captor.capture());

        final JsonNode msgNode = captor.getValue().asJson();
        assertEquals(msgNode.get("message").textValue(),
            NOT_A_SCHEMA.toString());
    }

    @Test
    public void unknownKeywordsAreReportedAsWarnings()
        throws ProcessingException
    {
        final ObjectNode node = FACTORY.objectNode();
        node.put("foo", "");
        node.put("bar", "");

        final ValidationData data = schemaToData(node);

        final ArrayNode ignored = FACTORY.arrayNode();
        // They appear in alphabetical order in the report!
        ignored.add("bar");
        ignored.add("foo");

        final ArgumentCaptor<ProcessingMessage> captor
            = ArgumentCaptor.forClass(ProcessingMessage.class);

        processor.process(report, data);
        verify(report).log(captor.capture());

        final ProcessingMessage message = captor.getValue();
        assertEquals(message.getLogLevel(), LogLevel.WARNING);

        final JsonNode msgNode = message.asJson();
        assertEquals(msgNode.get("message").textValue(),
            UNKNOWN_KEYWORDS.toString());
        assertEquals(msgNode.get("ignored"), ignored);
    }

    @Test
    public void equivalentSchemasAreNotCheckedTwice()
        throws ProcessingException
    {
        final ObjectNode node = FACTORY.objectNode();
        node.put(K1, K1);

        final ValidationData data = schemaToData(node);

        processor.process(report, data);
        processor.process(report, data);

        verify(checker, onlyOnce()).checkSyntax(eq(processor), anyReport(),
            anySchema());
    }

    @Test
    public void errorsAreCorrectlyReported()
        throws ProcessingException
    {
        final ProcessingMessage msg = new ProcessingMessage().msg(ERRMSG)
            .setLogLevel(LogLevel.ERROR);

        final ObjectNode schema = FACTORY.objectNode();
        schema.put(K2, "");

        final ValidationData data = schemaToData(schema);

        processor.process(report, data);

        verify(report).log(eq(msg));
    }

    @Test
    public void checkingWillNotDiveIntoUnknownKeywords()
        throws ProcessingException
    {
        final ObjectNode node = FACTORY.objectNode();
        node.put(K1, K1);
        final ObjectNode schema = FACTORY.objectNode();
        schema.put("foo", node);
        final ValidationData data = schemaToData(schema);

        processor.process(report, data);
        verify(checker, never()).checkSyntax(eq(processor), anyReport(),
            anySchema());
    }

    @Test
    public void divingIntoAnUnknownPathPerformsChecking()
        throws ProcessingException
    {
        final ObjectNode inner = FACTORY.objectNode();
        inner.put(K1, "");
        final ObjectNode schema = FACTORY.objectNode();
        schema.put("foo", inner);

        final ValidationData data = schemaToData(schema);
        data.getSchema().setPointer(JsonPointer.empty().append("foo"));

        processor.process(report, data);
        verify(checker).checkSyntax(eq(processor), anyReport(), anySchema());
    }

    @Test
    public void divingIntoAKnownPathDoesNotPerformCheckingAgain()
        throws ProcessingException
    {
        final ObjectNode inner = FACTORY.objectNode();
        inner.put(K1, "");
        final ObjectNode schema = FACTORY.objectNode();
        schema.put(K1, inner);
        schema.put("foo", "bar");

        final ValidationData data = schemaToData(schema);

        processor.process(report, data);
        data.getSchema().setPointer(JsonPointer.empty().append(K1));
        processor.process(report, data);
        verify(checker, onlyOnce()).checkSyntax(eq(processor), anyReport(),
            anySchema());
    }

    private static ValidationData schemaToData(final JsonNode schema)
    {
        final CanonicalSchemaTree tree = new CanonicalSchemaTree(schema);
        return new ValidationData(tree);
    }
}
