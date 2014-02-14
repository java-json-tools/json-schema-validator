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

package com.github.fge.jsonschema.keyword.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.messages.JsonSchemaValidationBundle;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.JsonTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.tree.SimpleJsonTree;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.collect.Lists;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import static com.github.fge.jsonschema.TestUtils.*;
import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public abstract class AbstractKeywordValidatorTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaValidationBundle.class);

    private final String keyword;
    private final Constructor<? extends KeywordValidator> constructor;
    private final JsonNode testNode;

    protected AbstractKeywordValidatorTest(
        final Dictionary<Constructor<? extends KeywordValidator>> dict,
        final String prefix, final String keyword)
        throws IOException
    {
        this.keyword = keyword;
        constructor = dict.entries().get(keyword);
        final String resourceName
            = String.format("/keyword/validators/%s/%s.json", prefix, keyword);
        testNode = JsonLoader.fromResource(resourceName);
    }

    @Test
    public final void keywordExists()
    {
        assertNotNull(constructor, "no support for " + keyword + "??");
    }

    @DataProvider
    protected final Iterator<Object[]> getValueTests()
    {
        final List<Object[]> list = Lists.newArrayList();

        String msg;
        JsonNode msgNode, msgData, msgParams;

        for (final JsonNode node: testNode) {
            msgNode = node.get("message");
            msgData = node.get("msgData");
            msgParams = node.get("msgParams");
            msg = msgNode == null ? null
                : buildMessage(msgNode.textValue(), msgParams, msgData);
            list.add(new Object[]{ node.get("digest"), node.get("data"), msg,
                node.get("valid").booleanValue(), msgData });
        }

        return list.iterator();
    }

    // Unfortunately, the suppress warning annotation is needed
    @Test(dataProvider = "getValueTests", dependsOnMethods = "keywordExists")
    public final void instancesAreValidatedCorrectly(final JsonNode digest,
        final JsonNode node, final String msg, final boolean valid,
        final ObjectNode msgData)
        throws IllegalAccessException, InvocationTargetException,
        InstantiationException, ProcessingException
    {
        // FIXME: dummy, but we have no choice
        final SchemaTree tree = new CanonicalSchemaTree(digest);
        final JsonTree instance = new SimpleJsonTree(node);
        final FullData data = new FullData(tree, instance);

        final ProcessingReport report = mock(ProcessingReport.class);
        @SuppressWarnings("unchecked")
        final Processor<FullData, FullData> processor = mock(Processor.class);

        final KeywordValidator validator = constructor.newInstance(digest);
        validator.validate(processor, report, BUNDLE, data);

        if (valid) {
            verify(report, never()).error(anyMessage());
            return;
        }

        final ArgumentCaptor<ProcessingMessage> captor
            = ArgumentCaptor.forClass(ProcessingMessage.class);

        verify(report).error(captor.capture());

        final ProcessingMessage message = captor.getValue();

        assertMessage(message).isValidationError(keyword, msg)
            .hasContents(msgData);
    }

    private static String buildMessage(final String key, final JsonNode params,
        final JsonNode data)
    {
        final ProcessingMessage message = new ProcessingMessage()
            .setMessage(BUNDLE.getMessage(key));
        if (params != null) {
            String name;
            JsonNode value;
            for (final JsonNode node: params) {
                name = node.textValue();
                value = data.get(name);
                message.putArgument(name, valueToArgument(value));
            }
        }
        return message.getMessage();
    }

    private static Object valueToArgument(final JsonNode value)
    {
        final NodeType type = NodeType.getNodeType(value);

        switch (type) {
            case STRING:
                return value.textValue();
            case INTEGER:
                return value.bigIntegerValue();
            case NUMBER: case NULL: case OBJECT: case ARRAY:
                return value;
            case BOOLEAN:
                return value.booleanValue();
//            case ARRAY:
//                final List<Object> list = Lists.newArrayList();
//                for (final JsonNode element: value)
//                    list.add(valueToArgument(element));
//                return list;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
