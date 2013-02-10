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

package com.github.fge.jsonschema.crude;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.report.AbstractProcessingReport;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.AsJson;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

public final class UncheckedReport
    extends AbstractProcessingReport
    implements AsJson
{
    private final List<ProcessingMessage> messages = Lists.newArrayList();

    UncheckedReport(final ProcessingException e,
        final ProcessingReport report)
    {
        messages.add(e.getProcessingMessage()
            .put("info", "other messages follow (if any)"));
        for (final ProcessingMessage message: report.getMessages())
            messages.add(message);
    }

    @Override
    public void doLog(final ProcessingMessage message)
    {
    }

    @Override
    public List<ProcessingMessage> getMessages()
    {
        return ImmutableList.copyOf(messages);
    }

    @Override
    public JsonNode asJson()
    {
        final ArrayNode ret = JacksonUtils.nodeFactory().arrayNode();
        for (final ProcessingMessage message: messages)
            ret.add(message.asJson());
        return ret;
    }
}
