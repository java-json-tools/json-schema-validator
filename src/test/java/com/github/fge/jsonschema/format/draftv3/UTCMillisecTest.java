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

package com.github.fge.jsonschema.format.draftv3;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.tree.JsonTree;
import com.github.fge.jsonschema.tree.SimpleJsonTree;
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
