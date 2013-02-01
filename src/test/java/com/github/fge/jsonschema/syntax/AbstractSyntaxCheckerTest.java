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

package com.github.fge.jsonschema.syntax;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.SampleNodeProvider;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.syntax.SyntaxProcessor;
import com.github.fge.jsonschema.processing.syntax.SyntaxReport;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.List;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static com.github.fge.jsonschema.messages.SyntaxMessages.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class AbstractSyntaxCheckerTest
{
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();
    private static final String KEYWORD = "foo";

    @DataProvider
    public Iterator<Object[]> validTypes()
    {
        return SampleNodeProvider.getSamples(NodeType.ARRAY, NodeType.INTEGER,
            NodeType.STRING);
    }

    @Test(dataProvider = "validTypes")
    public void syntaxCheckingSucceedsOnValidTypes(final JsonNode node)
        throws ProcessingException
    {
        final AbstractSyntaxChecker checker = spy(new DummyChecker());
        final SyntaxReport report = new SyntaxReport();
        final ObjectNode schema = FACTORY.objectNode();
        schema.put(KEYWORD, node);
        final JsonSchemaTree tree = new CanonicalSchemaTree(schema);

        checker.checkSyntax(null, report, tree);
        verify(checker).checkValue(null, report, tree);
        assertTrue(report.getMessages().isEmpty());
    }

    @DataProvider
    public Iterator<Object[]> invalidTypes()
    {
        return SampleNodeProvider.getSamplesExcept(NodeType.ARRAY,
            NodeType.INTEGER, NodeType.STRING);
    }

    @Test(dataProvider = "invalidTypes")
    public void syntaxCheckingFailsOnValidTypes(final JsonNode node)
        throws ProcessingException
    {
        final AbstractSyntaxChecker checker = spy(new DummyChecker());
        final SyntaxReport report = new SyntaxReport();
        final ObjectNode schema = FACTORY.objectNode();
        schema.put(KEYWORD, node);
        final JsonSchemaTree tree = new CanonicalSchemaTree(schema);

        checker.checkSyntax(null, report, tree);
        verify(checker, never()).checkValue(null, report, tree);

        final List<ProcessingMessage> messages = report.getMessages();
        assertEquals(messages.size(), 1);

        final ProcessingMessage msg = messages.get(0);
        assertMessage(msg).hasField("keyword", KEYWORD).hasField("schema", tree)
            .hasMessage(INCORRECT_TYPE).hasField("domain", "syntax");
    }

    private static class DummyChecker
        extends AbstractSyntaxChecker
    {
        private DummyChecker()
        {
            super(KEYWORD, NodeType.ARRAY, NodeType.INTEGER, NodeType.STRING);
        }

        @Override
        protected void checkValue(final SyntaxProcessor processor,
            final SyntaxReport report, final JsonSchemaTree tree)
            throws ProcessingException
        {
        }
    }
}
