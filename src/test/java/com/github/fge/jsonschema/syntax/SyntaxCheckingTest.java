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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.SampleNodeProvider;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.processing.syntax.SyntaxProcessor;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import com.google.common.collect.Sets;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Iterator;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static com.github.fge.jsonschema.messages.SyntaxMessages.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public abstract class SyntaxCheckingTest
{
    protected final SyntaxProcessor processor;
    protected final EnumSet<NodeType> invalidTypes;
    protected final boolean hasKeyword;
    protected final String prefix;
    protected final String keyword;

    protected SyntaxCheckingTest(final Dictionary<SyntaxChecker> dict,
        final String prefix, final String keyword, final NodeType first,
        final NodeType... other)
        throws IOException
    {
        this.prefix = prefix;
        this.keyword = keyword;
        processor = new SyntaxProcessor(dict);
        invalidTypes = Sets.complementOf(EnumSet.of(first, other));
        hasKeyword = dict.hasEntry(keyword);
    }

    @Test
    public final void keywordIsSupportedInThisDictionary()
    {
        assertTrue(hasKeyword, "keyword " + keyword + " is not supported??");
    }

    @DataProvider
    public final Iterator<Object[]> invalidTypes()
    {
        return SampleNodeProvider.getSamples(invalidTypes);
    }

    @Test(
        dependsOnMethods = "keywordIsSupportedInThisDictionary",
        dataProvider = "invalidTypes"
    )
    public final void invalidTypesAreReportedAsErrors(final JsonNode node)
        throws ProcessingException
    {
        final NodeType type = NodeType.getNodeType(node);
        final ArgumentCaptor<ProcessingMessage> captor
            = ArgumentCaptor.forClass(ProcessingMessage.class);
        final ProcessingReport report = mock(ProcessingReport.class);
        final ObjectNode schema = JacksonUtils.nodeFactory().objectNode();
        schema.put(keyword, node);
        final JsonSchemaTree tree = new CanonicalSchemaTree(schema);
        final ValidationData data = new ValidationData(tree);

        processor.process(report, data);

        verify(report).log(captor.capture());

        final ProcessingMessage msg = captor.getValue();
        assertMessage(msg).isSyntaxError(keyword, INCORRECT_TYPE, tree)
            .hasField("expected", EnumSet.complementOf(invalidTypes))
            .hasField("found", type);
    }
}
