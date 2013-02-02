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
import com.github.fge.jsonschema.processing.LogLevel;
import com.github.fge.jsonschema.util.AsJson;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;

public final class ProcessingMessage
    implements AsJson
{
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

    private final Map<String, JsonNode> map = Maps.newLinkedHashMap();

    private LogLevel level;

    public ProcessingMessage()
    {
        setLogLevel(LogLevel.INFO);
    }

    public ProcessingMessage setLogLevel(final LogLevel level)
    {
        this.level = Preconditions.checkNotNull(level,
            "log level cannot be null");
        return put("level", level);
    }

    public ProcessingMessage msg(final String message)
    {
        return put("message", message);
    }

    public <T> ProcessingMessage msg(final T value)
    {
        return put("message", value);
    }

    public ProcessingMessage put(final String key, final JsonNode value)
    {
        if (value == null)
            return putNull(key);
        map.put(key, value.deepCopy());
        return this;
    }

    public ProcessingMessage put(final String key, final AsJson asJson)
    {
        return put(key, asJson.asJson());
    }

    public ProcessingMessage put(final String key, final String value)
    {
        return value == null ? putNull(key) : put(key, FACTORY.textNode(value));
    }

    public ProcessingMessage put(final String key, final int value)
    {
        return put(key, FACTORY.numberNode(value));
    }

    public <T> ProcessingMessage put(final String key, final T value)
    {
        return value == null
            ? putNull(key)
            : put(key, FACTORY.textNode(value.toString()));
    }

    public <T> ProcessingMessage put(final String key,
        final Collection<T> values)
    {
        if (values == null)
            return putNull(key);
        final ArrayNode node = FACTORY.arrayNode();
        for (final T value: values)
            node.add(value == null
                ? FACTORY.nullNode()
                : FACTORY.textNode(value.toString()));
        return put(key, node);
    }

    public LogLevel getLogLevel()
    {
        return level;
    }

    private ProcessingMessage putNull(final String key)
    {
        map.put(key, FACTORY.nullNode());
        return this;
    }

    @Override
    public JsonNode asJson()
    {
        final ObjectNode ret = FACTORY.objectNode();
        ret.putAll(map);
        return ret;
    }

    @Override
    public int hashCode()
    {
        return map.hashCode();
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        final ProcessingMessage other = (ProcessingMessage) obj;
        return map.equals(other.map);
    }

    @Override
    public String toString()
    {
        return map.toString();
    }
}
