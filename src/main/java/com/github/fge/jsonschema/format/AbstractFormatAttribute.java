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

package com.github.fge.jsonschema.format;

import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.util.NodeType;

import java.util.EnumSet;

public abstract class AbstractFormatAttribute
    implements FormatAttribute
{
    private final EnumSet<NodeType> supported;
    private final String fmt;

    protected AbstractFormatAttribute(final String fmt, final NodeType first,
        final NodeType... other)
    {
        this.fmt = fmt;
        supported = EnumSet.of(first, other);
    }

    @Override
    public final EnumSet<NodeType> supportedTypes()
    {
        return EnumSet.copyOf(supported);
    }

    protected final <T> ProcessingMessage newMsg(final ValidationData data,
        final T message)
    {
        return data.newMessage().put("domain", "validation")
            .put("keyword", "format").msg(message).put("attribute", fmt);
    }
}
