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

package com.github.fge.jsonschema.matchers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.processing.LogLevel;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.github.fge.jsonschema.util.AsJson;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import org.fest.assertions.GenericAssert;

import java.util.Collection;
import java.util.Map;

import static org.fest.assertions.Assertions.*;
import static org.testng.Assert.*;

public final class ProcessingMessageAssert
    extends GenericAssert<ProcessingMessageAssert, ProcessingMessage>
{
    final JsonNode msg;

    public static ProcessingMessageAssert assertMessage(
        final ProcessingMessage message)
    {
        return new ProcessingMessageAssert(message);
    }

    private ProcessingMessageAssert(final ProcessingMessage actual)
    {
        super(ProcessingMessageAssert.class, actual);
        msg = actual.asJson();
    }

    /*
     * Simple asserts
     */
    public ProcessingMessageAssert hasField(final String name,
        final JsonNode value)
    {
        assertThat(msg.has(name)).isTrue();
        // We have to use assertEquals, otherwise it takes the node as a
        // Collection
        assertEquals(msg.get(name), value);
        return this;
    }

    public ProcessingMessageAssert hasField(final String name,
        final AsJson asJson)
    {
        return hasField(name, asJson.asJson());
    }

    public <T> ProcessingMessageAssert hasField(final String name,
        final T value)
    {
        assertThat(msg.has(name)).isTrue();
        final String input = msg.get(name).textValue();
        final String expected = value.toString();
        assertThat(input).isEqualTo(expected)
            .overridingErrorMessage("Strings differ: wanted " + expected
                + " but got " + input);
        return this;
    }

    public <T> ProcessingMessageAssert hasField(final String name,
        final Collection<T> value)
    {
        assertThat(msg.has(name)).isTrue();
        final JsonNode node = msg.get(name);
        assertThat(node.isArray()).isTrue();
        final ArrayNode input = JacksonUtils.nodeFactory().arrayNode();
        for (final T element: value)
            input.add(element.toString());
        assertEquals(node, input);
        return this;
    }

    public ProcessingMessageAssert hasTextField(final String name)
    {
        assertTrue(msg.path(name).isTextual());
        return this;
    }

    public ProcessingMessageAssert hasNullField(final String name)
    {
        assertThat(msg.has(name)).isTrue();
        assertEquals(msg.get(name), JacksonUtils.nodeFactory().nullNode());
        return this;
    }

    /*
     * Simple dedicated matchers
     */
    public ProcessingMessageAssert hasLevel(final LogLevel level)
    {
        assertThat(level).isEqualTo(actual.getLogLevel());
        return hasField("level", level);
    }

    public <T> ProcessingMessageAssert hasMessage(final T value)
    {
        return hasField("message", value);
    }

    public ProcessingMessageAssert hasMessage(final String expected)
    {
        final String message = msg.get("message").textValue();
        assertThat(message).isEqualTo(expected);
        return this;
    }

    /*
     * More complicated matchers
     */
    public <T> ProcessingMessageAssert isSyntaxError(final String keyword,
        final T msg, final JsonSchemaTree tree)
    {
        // FIXME: .hasLevel() is not always set
        return hasField("keyword", keyword).hasMessage(msg)
            .hasField("schema", tree).hasField("domain", "syntax");
    }

    /*
     * More complicated matchers
     */
    public <T> ProcessingMessageAssert isValidationError(final String keyword,
        final T msg, final ValidationData data)
    {
        // FIXME: .hasLevel() is not always set
        return hasField("keyword", keyword).hasMessage(msg)
            .hasField("domain", "validation")
            .hasField("schema", data.getSchema())
            .hasField("instance", data.getInstance());
    }

    public ProcessingMessageAssert hasContents(final ObjectNode node)
    {
        /*
         * No need to check if the map is empty
         */
        if (node.size() == 0)
            return this;

        /*
         * Grab the two nodes as maps
         */
        final Map<String, JsonNode> expectedMap = JacksonUtils.asMap(msg);
        final Map<String, JsonNode> actualMap = JacksonUtils.asMap(node);

        /*
         * Check that this message's map contains all keys of the wanted data
         */
        assertTrue(expectedMap.keySet().containsAll(actualMap.keySet()));

        /*
         * OK? Let's check contents with Map.equals().
         */
        expectedMap.keySet().retainAll(actualMap.keySet());
        assertEquals(actualMap, expectedMap, "different map contents");
        return this;
    }
}
