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

package com.github.fge.jsonschema.processors.syntax;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.SampleNodeProvider;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.keyword.syntax.SyntaxChecker;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.library.DictionaryBuilder;
import com.github.fge.jsonschema.processors.data.SchemaHolder;
import com.github.fge.jsonschema.report.AbstractProcessingReport;
import com.github.fge.jsonschema.report.LogLevel;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.github.fge.jsonschema.util.NodeType;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;

import static com.github.fge.jsonschema.TestUtils.*;
import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static com.github.fge.jsonschema.messages.SyntaxMessages.*;
import static org.mockito.Mockito.*;

public final class SyntaxProcessorTest
{
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();
    private static final String K1 = "k1";
    private static final String K2 = "k2";
    private static final String ERRMSG = "foo";

    private AbstractProcessingReport report;
    private SyntaxProcessor processor;
    private SyntaxChecker checker;

    @BeforeMethod
    public void initialize()
    {
        report = spy(new TestProcessingReport());
        final DictionaryBuilder<SyntaxChecker> builder
            = Dictionary.newBuilder();

        checker = mock(SyntaxChecker.class);
        builder.addEntry(K1, checker);
        builder.addEntry(K2, new SyntaxChecker()
        {
            @Override
            public EnumSet<NodeType> getValidTypes()
            {
                return EnumSet.noneOf(NodeType.class);
            }

            @Override
            public void checkSyntax(final Collection<JsonPointer> pointers,
                final ProcessingReport report, final SchemaTree tree)
                throws ProcessingException
            {
                report.error(new ProcessingMessage().message(ERRMSG));
            }
        });

        processor = new SyntaxProcessor(builder.freeze());
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

        final SchemaHolder holder = schemaToData(node);

        processor.process(report, holder);

        verify(report).log(same(LogLevel.ERROR), captor.capture());

        final ProcessingMessage message = captor.getValue();
        assertMessage(message).hasMessage(NOT_A_SCHEMA)
        .hasField("found", NodeType.getNodeType(node));
    }

    @Test
    public void unknownKeywordsAreReportedAsWarnings()
        throws ProcessingException
    {
        final ObjectNode node = FACTORY.objectNode();
        node.put("foo", "");
        node.put("bar", "");

        final SchemaHolder holder = schemaToData(node);

        final ArrayNode ignored = FACTORY.arrayNode();
        // They appear in alphabetical order in the report!
        ignored.add("bar");
        ignored.add("foo");

        final ArgumentCaptor<ProcessingMessage> captor
            = ArgumentCaptor.forClass(ProcessingMessage.class);

        processor.process(report, holder);
        verify(report).log(same(LogLevel.WARNING), captor.capture());

        final ProcessingMessage message = captor.getValue();

        assertMessage(message).hasMessage(UNKNOWN_KEYWORDS)
            .hasField("ignored", ignored);
    }

    @Test
    public void errorsAreCorrectlyReported()
        throws ProcessingException
    {
        final ArgumentCaptor<ProcessingMessage> captor
            = ArgumentCaptor.forClass(ProcessingMessage.class);

        final ObjectNode schema = FACTORY.objectNode();
        schema.put(K2, "");

        final SchemaHolder holder = schemaToData(schema);

        processor.process(report, holder);

        verify(report).log(same(LogLevel.ERROR), captor.capture());

        final ProcessingMessage msg = captor.getValue();
        assertMessage(msg).hasMessage(ERRMSG);
    }

    @Test
    public void checkingWillNotDiveIntoUnknownKeywords()
        throws ProcessingException
    {
        final ObjectNode node = FACTORY.objectNode();
        node.put(K1, K1);
        final ObjectNode schema = FACTORY.objectNode();
        schema.put("foo", node);
        final SchemaHolder holder = schemaToData(schema);

        processor.process(report, holder);
        verify(checker, never()).checkSyntax(anyCollectionOf(JsonPointer.class),
            anyReport(), anySchema());
    }

    private static SchemaHolder schemaToData(final JsonNode schema)
    {
        final SchemaTree tree = new CanonicalSchemaTree(schema);
        return new SchemaHolder(tree);
    }

    private static class TestProcessingReport
        extends AbstractProcessingReport
    {
        @Override
        public void log(final LogLevel level, final ProcessingMessage message)
        {
        }
    }
}