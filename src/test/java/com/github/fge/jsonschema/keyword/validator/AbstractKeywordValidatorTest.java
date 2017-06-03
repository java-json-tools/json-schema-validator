/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.keyword.validator;

import static com.github.fge.jsonschema.TestUtils.anyMessage;
import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.assertMessage;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import org.mockito.ArgumentCaptor;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.core.tree.JsonTree;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.core.tree.SimpleJsonTree;
import com.github.fge.jsonschema.core.tree.key.SchemaKey;
import com.github.fge.jsonschema.core.util.Dictionary;
import com.github.fge.jsonschema.messages.JsonSchemaValidationBundle;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.collect.Lists;

@Test
public abstract class AbstractKeywordValidatorTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaValidationBundle.class);

    private final String keyword;
    private final KeywordValidatorFactory factory;
    private final JsonNode testNode;

    protected AbstractKeywordValidatorTest(
        final Dictionary<KeywordValidatorFactory> dict,
        final String prefix, final String keyword)
        throws IOException
    {
        this.keyword = keyword;
        factory = dict.entries().get(keyword);
        final String resourceName
            = String.format("/keyword/validators/%s/%s.json", prefix, keyword);
        testNode = JsonLoader.fromResource(resourceName);
    }

    @Test
    public final void keywordExists()
    {
        assertNotNull(factory, "no support for " + keyword + "??");
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
        final SchemaTree tree
            = new CanonicalSchemaTree(SchemaKey.anonymousKey(), digest);
        final JsonTree instance = new SimpleJsonTree(node);
        final FullData data = new FullData(tree, instance);

        final ProcessingReport report = mock(ProcessingReport.class);
        @SuppressWarnings("unchecked")
        final Processor<FullData, FullData> processor = mock(Processor.class);

        final KeywordValidator validator = factory.getKeywordValidator(digest);
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
