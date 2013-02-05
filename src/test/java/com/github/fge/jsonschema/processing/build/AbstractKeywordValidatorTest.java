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

package com.github.fge.jsonschema.processing.build;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.keyword.validators.KeywordValidator;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.messages.KeywordValidationMessages;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.github.fge.jsonschema.tree.JsonTree;
import com.github.fge.jsonschema.tree.SimpleJsonTree;
import com.github.fge.jsonschema.util.JsonLoader;
import com.github.fge.jsonschema.util.ProcessingCache;
import com.google.common.collect.Lists;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static com.github.fge.jsonschema.TestUtils.*;
import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public abstract class AbstractKeywordValidatorTest
{
    protected final Dictionary<KeywordDescriptor> dict;
    protected final String keyword;
    protected final KeywordDescriptor descriptor;
    protected final ProcessingCache<JsonNode, KeywordValidator> cache;
    protected final JsonNode testNode;

    protected AbstractKeywordValidatorTest(
        final Dictionary<KeywordDescriptor> dict, final String prefix,
        final String keyword)
        throws IOException
    {
        this.dict = dict;
        this.keyword = keyword;
        descriptor = dict.get(keyword);
        cache = descriptor == null ? null : descriptor.buildCache();
        final String resourceName
            = String.format("/keyword/validators/%s/%s.json", prefix, keyword);
        testNode = JsonLoader.fromResource(resourceName);
    }

    @Test
    public final void keywordExists()
    {
        assertNotNull(descriptor, "no support for " + keyword + "??");
    }

    @DataProvider
    protected final Iterator<Object[]> getValueTests()
    {

        final List<Object[]> list = Lists.newArrayList();

        KeywordValidationMessages msg;
        JsonNode msgNode;

        for (final JsonNode node: testNode) {
            msgNode = node.get("message");
            msg = msgNode == null ? null
                : KeywordValidationMessages.valueOf(msgNode.textValue());
            list.add(new Object[]{ node.get("schema"), node.get("data"), msg,
                node.get("valid").booleanValue(), node.get("msgData") });
        }
        return list.iterator();
    }

    // Unfortunately, the suppress warning annotation is needed
    @Test(dataProvider = "getValueTests", dependsOnMethods = "keywordExists")
    public final void instancesAreValidatedCorrectly(final JsonNode schema,
        final JsonNode node, final KeywordValidationMessages msg,
        final boolean valid, final ObjectNode msgData)
        throws ProcessingException
    {
        final JsonSchemaTree tree = new CanonicalSchemaTree(schema);
        final JsonTree instance = new SimpleJsonTree(node);
        final ValidationData data = new ValidationData(tree, instance);

        final ProcessingReport report = mock(ProcessingReport.class);
        @SuppressWarnings("unchecked")
        final Processor<ValidationData, ProcessingReport> processor
            =  mock(Processor.class);

        final KeywordValidator validator = cache.get(schema);
        validator.validate(processor, report, data);

        if (valid) {
            verify(report, never()).error(anyMessage());
            return;
        }

        final ArgumentCaptor<ProcessingMessage> captor
            = ArgumentCaptor.forClass(ProcessingMessage.class);

        verify(report).error(captor.capture());

        final ProcessingMessage message = captor.getValue();

        assertMessage(message).isValidationError(keyword, msg, data)
            .hasContents(msgData);
    }
}
