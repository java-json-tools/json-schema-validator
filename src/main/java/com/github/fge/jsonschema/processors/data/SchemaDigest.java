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

package com.github.fge.jsonschema.processors.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.report.MessageProvider;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

public final class SchemaDigest
    implements MessageProvider
{
    private final SchemaContext context;
    private final Map<String, JsonNode> digested;

    public SchemaDigest(final SchemaContext context,
        final Map<String, JsonNode> map)
    {
        this.context = context;
        digested = ImmutableMap.copyOf(map);
    }

    public SchemaContext getContext()
    {
        return context;
    }

    public Map<String, JsonNode> getDigests()
    {
        return digested;
    }

    @Override
    public ProcessingMessage newMessage()
    {
        return context.newMessage();
    }
}
