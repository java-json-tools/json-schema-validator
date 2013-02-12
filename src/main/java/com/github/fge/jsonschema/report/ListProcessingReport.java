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
import com.github.fge.jsonschema.util.AsJson;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.google.common.collect.Lists;

import java.util.List;

public class ListProcessingReport
    extends AbstractProcessingReport
    implements AsJson
{
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

    private final List<ProcessingMessage> messages = Lists.newArrayList();

    public ListProcessingReport()
    {
    }

    public ListProcessingReport(final ProcessingReport other)
    {
        // FIXME: necessary, otherwise mocks don't work
        LogLevel level;
        level = other.getLogLevel();
        if (level != null)
            setLogLevel(level);
        level = other.getExceptionThreshold();
        if (level != null)
            setExceptionThreshold(level);
    }

    @Override
    public final void doLog(final ProcessingMessage message)
    {
        messages.add(message);
    }

    @Override
    public final JsonNode asJson()
    {
        final ArrayNode ret = FACTORY.arrayNode();
        for (final ProcessingMessage message: messages)
            ret.add(message.asJson());
        return ret;
    }

    @Override
    public final List<ProcessingMessage> getMessages()
    {
        return Lists.newArrayList(messages);
    }
}
