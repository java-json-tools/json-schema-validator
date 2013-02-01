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
import com.github.fge.jsonschema.processing.LogLevel;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import org.fest.assertions.GenericAssert;

import static org.fest.assertions.Assertions.assertThat;
import static org.testng.Assert.*;

public final class ProcessingMessageAssert
    extends GenericAssert<ProcessingMessageAssert, ProcessingMessage>
{
    final JsonNode messageContents;

    public ProcessingMessageAssert(final ProcessingMessage actual)
    {
        super(ProcessingMessageAssert.class, actual);
        messageContents = actual.asJson();
    }

    public static ProcessingMessageAssert assertMessage(
        final ProcessingMessage message)
    {
        return new ProcessingMessageAssert(message);
    }

    public ProcessingMessageAssert hasLevel(final LogLevel level)
    {
        assertThat(level).isEqualTo(actual.getLogLevel());
        return this;
    }

    public <E extends Enum<E>> ProcessingMessageAssert hasMessage(final E value)
    {
        final String msg = messageContents.get("message").textValue();
        final String expected = value.toString();

        assertThat(msg).isEqualTo(expected);
        return this;
    }

    public ProcessingMessageAssert hasMessage(final String expected)
    {
        final String msg = messageContents.get("message").textValue();
        assertThat(msg).isEqualTo(expected);
        return this;
    }

    public ProcessingMessageAssert hasField(final String name,
        final JsonNode value)
    {
        assertThat(messageContents.has(name)).isTrue();
        // We have to use assertEquals, otherwise it takes the node as a
        // Collection
        assertEquals(messageContents.get(name), value);
        return this;
    }

    public ProcessingMessageAssert hasTextField(final String name,
        final String value)
    {
        assertThat(messageContents.has(name)).isTrue();
        assertThat(messageContents.get(name).textValue()).isEqualTo(value);
        return this;
    }

    public ProcessingMessageAssert hasNullField(final String name)
    {
        assertThat(messageContents.has(name)).isTrue();
        assertEquals(messageContents.get(name),
            JacksonUtils.nodeFactory().nullNode());
        return this;
    }
}
