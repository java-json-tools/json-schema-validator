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

package com.github.fge.jsonschema.format.draftv3;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.tree.JsonTree;
import com.github.fge.jsonschema.core.tree.SimpleJsonTree;
import com.github.fge.jsonschema.processors.data.FullData;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.mockito.Mockito.*;

public final class UTCMillisecTest
    extends DraftV3FormatAttributeTest
{
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();
    public UTCMillisecTest()
        throws IOException
    {
        super("utc-millisec");
    }

    // FIXME: why do I have to do that? :(
    @Test
    public void retestAttribute()
    {
        formatAttributeIsSupported();
    }

    @DataProvider
    public Iterator<Object[]> negativeValues()
    {
        final List<? extends JsonNode> list = ImmutableList.of(
            FACTORY.numberNode(new BigDecimal("-928019283")),
            FACTORY.numberNode(new BigDecimal("-928019283.01")),
            FACTORY.numberNode(-1)
        );

        return Iterables.transform(list, new Function<JsonNode, Object[]>()
        {
            @Override
            public Object[] apply(final JsonNode input)
            {
                return new Object[] { input };
            }
        }).iterator();
    }

    @Test(
        dependsOnMethods = "retestAttribute",
        dataProvider = "negativeValues"
    )
    public void userIsWarnedAboutNegativeEpochs(final JsonNode input)
        throws ProcessingException
    {
        final JsonTree tree = new SimpleJsonTree(input);
        final FullData data = new FullData(SCHEMA_TREE, tree);

        attribute.validate(report, BUNDLE, data);

        final ArgumentCaptor<ProcessingMessage> captor
            = ArgumentCaptor.forClass(ProcessingMessage.class);

        verify(report, only()).warn(captor.capture());

        final ProcessingMessage message = captor.getValue();

        assertMessage(message).isFormatMessage(fmt,
            BUNDLE.printf("warn.format.epoch.negative", input));
    }

    @DataProvider
    public Iterator<Object[]> overflows()
    {
        final List<? extends JsonNode> list = ImmutableList.of(
            FACTORY.numberNode(new BigDecimal("2147483648000")),
            FACTORY.numberNode(new BigDecimal("2147483648000.2983"))
        );

        return Iterables.transform(list, new Function<JsonNode, Object[]>()
        {
            @Override
            public Object[] apply(final JsonNode input)
            {
                return new Object[] { input };
            }
        }).iterator();
    }

    @Test(
        dependsOnMethods = "retestAttribute",
        dataProvider = "overflows"
    )
    public void userIsWarnedAboutPotentialOverflows(final JsonNode input)
        throws ProcessingException
    {
        final JsonTree tree = new SimpleJsonTree(input);
        final FullData data = new FullData(SCHEMA_TREE, tree);

        attribute.validate(report, BUNDLE, data);

        final ArgumentCaptor<ProcessingMessage> captor
            = ArgumentCaptor.forClass(ProcessingMessage.class);

        verify(report, only()).warn(captor.capture());

        final ProcessingMessage message = captor.getValue();

        assertMessage(message)
            .isFormatMessage(fmt, BUNDLE.printf("warn.format.epoch.overflow",
                input));
    }
}
