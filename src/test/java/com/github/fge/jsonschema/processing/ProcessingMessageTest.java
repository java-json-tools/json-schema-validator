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

package com.github.fge.jsonschema.processing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import com.google.common.collect.Lists;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.testng.Assert.*;

public final class ProcessingMessageTest
{
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

    @Test
    public void defaultLogThresholdIsInfo()
    {
        final ProcessingMessage msg = new ProcessingMessage();
        final JsonNode node = msg.asJson().get("level");

        assertSame(msg.getThreshold(), LogThreshold.INFO);
        assertEquals(node.textValue(), LogThreshold.INFO.toString());
    }

    @Test
    public void settingLogThresholdWorks()
    {
        final ProcessingMessage msg = new ProcessingMessage();

        JsonNode node;

        for (final LogThreshold threshold: LogThreshold.values()) {
            msg.setLogThreshold(threshold);
            node = msg.asJson().get("level");
            assertSame(msg.getThreshold(), threshold);
            assertEquals(node.textValue(), threshold.toString());
        }
    }

    @Test
    public void cannoSetThresholdToNull()
    {
        final ProcessingMessage msg = new ProcessingMessage();

        try {
            msg.setLogThreshold(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "log threshold cannot be null");
        }
    }

    @Test
    public void msgMethodSetsMessageField()
    {
        final ProcessingMessage msg = new ProcessingMessage().msg("foo");
        final JsonNode node = msg.asJson().path("message");

        assertTrue(node.isTextual());
        assertEquals(node.textValue(), "foo");
    }

    @Test
    public void settingStringFieldWorks()
    {
        final ProcessingMessage msg = new ProcessingMessage().put("foo", "bar");
        final JsonNode node = FACTORY.textNode("bar");

        assertEquals(msg.asJson().get("foo"), node);
    }

    @Test(dependsOnMethods = "settingStringFieldWorks")
    public void settingNullStringSetsNullNode()
    {
        final ProcessingMessage msg = new ProcessingMessage()
            .put("foo", (String) null);

        assertEquals(msg.asJson().get("foo"), FACTORY.nullNode());
    }

    @Test
    public void settingAnyObjectSetsToString()
    {
        final Object foo = new Object();
        final JsonNode node = FACTORY.textNode(foo.toString());
        final ProcessingMessage msg = new ProcessingMessage().put("foo", foo);

        assertEquals(msg.asJson().get("foo"), node);
    }

    @Test(dependsOnMethods = "settingAnyObjectSetsToString")
    public void settingNullObjectSetsNullNode()
    {
        final Object o = null;
        final ProcessingMessage msg = new ProcessingMessage().put("foo", o);

        assertEquals(msg.asJson().get("foo"), FACTORY.nullNode());
    }

    @Test
    public void settingAnyJsonNodeWorks()
    {
        final JsonNode foo = FACTORY.booleanNode(true);
        final ProcessingMessage msg = new ProcessingMessage().put("foo", foo);

        assertEquals(msg.asJson().get("foo"), foo);
    }

    @Test(dependsOnMethods = "settingAnyJsonNodeWorks")
    public void nodesAreUnalteredWhenSubmitted()
    {
        final ObjectNode foo = FACTORY.objectNode();
        foo.put("a", "b");
        final JsonNode node = foo.deepCopy();

        final ProcessingMessage msg = new ProcessingMessage().put("foo", foo);
        foo.remove("a");

        assertEquals(msg.asJson().get("foo"), node);
    }

    @Test(dependsOnMethods = "settingAnyJsonNodeWorks")
    public void settingNullJsonNodeSetsNullNode()
    {
        final JsonNode node = null;
        final ProcessingMessage msg = new ProcessingMessage().put("foo", node);

        assertEquals(msg.asJson().get("foo"), FACTORY.nullNode());
    }

    @Test
    public void submittedCollectionAppliesToStringToElements()
    {
        final List<Object> list = Arrays.asList(new Object(), new Object());
        final ArrayNode node = FACTORY.arrayNode();
        for (final Object o: list)
            node.add(o.toString());

        final ProcessingMessage msg = new ProcessingMessage().put("foo", list);

        assertEquals(msg.asJson().get("foo"), node);
    }

    @Test(dependsOnMethods = "submittedCollectionAppliesToStringToElements")
    public void submittingNullCollectionSetsNullNode()
    {
        final Collection<Object> foo = null;
        final ProcessingMessage msg = new ProcessingMessage().put("foo", foo);

        assertEquals(msg.asJson().get("foo"), FACTORY.nullNode());
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

        assertEquals(msg.asJson().get("foo"), node);
    }
}
