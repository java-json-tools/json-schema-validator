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

package com.github.fge.jsonschema.report;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.exceptions.ExceptionProvider;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.google.common.collect.Lists;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.testng.Assert.*;

public final class ProcessingMessageTest
{
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

    @Test
    public void defaultLogLevelIsInfo()
    {
        final ProcessingMessage msg = new ProcessingMessage();
        assertMessage(msg).hasLevel(LogLevel.INFO);
    }

    @Test
    public void settingLogThresholdWorks()
    {
        final ProcessingMessage msg = new ProcessingMessage();

        for (final LogLevel level: LogLevel.values()) {
            msg.setLogLevel(level);
            assertMessage(msg).hasLevel(level);
        }
    }

    @Test
    public void cannoSetThresholdToNull()
    {
        final ProcessingMessage msg = new ProcessingMessage();

        try {
            msg.setLogLevel(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "log level cannot be null");
        }
    }

    @Test
    public void msgMethodSetsMessageField()
    {
        final ProcessingMessage msg = new ProcessingMessage().message("foo");
        assertMessage(msg).hasMessage("foo");
    }

    @Test
    public void settingStringFieldWorks()
    {
        final ProcessingMessage msg = new ProcessingMessage().put("foo", "bar");

        assertMessage(msg).hasField("foo", "bar");
    }

    @Test(dependsOnMethods = "settingStringFieldWorks")
    public void settingNullStringSetsNullNode()
    {
        final ProcessingMessage msg = new ProcessingMessage()
            .put("foo", (String) null);

        assertMessage(msg).hasNullField("foo");
    }

    @Test
    public void settingAnyObjectSetsToString()
    {
        final Object foo = new Object();
        final JsonNode node = FACTORY.textNode(foo.toString());
        final ProcessingMessage msg = new ProcessingMessage().put("foo", foo);

        assertMessage(msg).hasField("foo", node);
    }

    @Test(dependsOnMethods = "settingAnyObjectSetsToString")
    public void settingNullObjectSetsNullNode()
    {
        final Object o = null;
        final ProcessingMessage msg = new ProcessingMessage().put("foo", o);

        assertMessage(msg).hasNullField("foo");
    }

    @Test
    public void settingAnyJsonNodeWorks()
    {
        final JsonNode foo = FACTORY.booleanNode(true);
        final ProcessingMessage msg = new ProcessingMessage().put("foo", foo);

        assertMessage(msg).hasField("foo", foo);
    }

    @Test(dependsOnMethods = "settingAnyJsonNodeWorks")
    public void nodesAreUnalteredWhenSubmitted()
    {
        final ObjectNode foo = FACTORY.objectNode();
        foo.put("a", "b");
        final JsonNode node = foo.deepCopy();

        final ProcessingMessage msg = new ProcessingMessage().put("foo", foo);
        foo.remove("a");

        assertMessage(msg).hasField("foo", node);
    }

    @Test(dependsOnMethods = "settingAnyJsonNodeWorks")
    public void settingNullJsonNodeSetsNullNode()
    {
        final JsonNode node = null;
        final ProcessingMessage msg = new ProcessingMessage().put("foo", node);

        assertMessage(msg).hasNullField("foo");
    }

    @Test
    public void submittedCollectionAppliesToStringToElements()
    {
        final List<Object> list = Arrays.asList(new Object(), new Object());
        final ArrayNode node = FACTORY.arrayNode();
        for (final Object o: list)
            node.add(o.toString());

        final ProcessingMessage msg = new ProcessingMessage().put("foo", list);

        assertMessage(msg).hasField("foo", node);
    }

    @Test(dependsOnMethods = "submittedCollectionAppliesToStringToElements")
    public void submittingNullCollectionSetsNullNode()
    {
        final Collection<Object> foo = null;
        final ProcessingMessage msg = new ProcessingMessage().put("foo", foo);

        assertMessage(msg).hasNullField("foo");
    }

    @Test(dependsOnMethods = "submittedCollectionAppliesToStringToElements")
    public void nullElementInCollectionSetsNullNode()
    {
        final List<Object> list = Lists.newArrayList(
            new Object(), null, new Object());
        final ArrayNode node = FACTORY.arrayNode();
        node.add(list.get(0).toString());
        node.add(FACTORY.nullNode());
        node.add(list.get(2).toString());

        final ProcessingMessage msg = new ProcessingMessage().put("foo", list);

        assertMessage(msg).hasField("foo", node);
    }

    @Test
    public void settingExceptionProviderYieldsCorrectException()
        throws ProcessingException
    {
        final ProcessingMessage testMessage = new ProcessingMessage();
        testMessage.setExceptionProvider(new ExceptionProvider()
        {
            @Override
            public ProcessingException doException(
                final ProcessingMessage message)
            {
                return new Foo(message);
            }
        });

        try {
            throw testMessage.asException();
        } catch (Foo ignored) {
            assertTrue(true);
        }
    }

    private static final class Foo
        extends ProcessingException
    {
        private Foo(final ProcessingMessage message)
        {
            super(message);
        }
    }
}
