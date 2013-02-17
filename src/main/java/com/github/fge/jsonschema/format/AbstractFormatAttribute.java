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

import com.github.fge.jsonschema.messages.FormatMessages;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.util.NodeType;

import java.util.EnumSet;

/**
 * Base abstract class for a format attribute
 *
 * <p>You should really use this class instead of implementing {@link
 * FormatAttribute} directly. Its main, but important, helping role is to
 * build the list of supported types for you.</p>
 */
public abstract class AbstractFormatAttribute
    implements FormatAttribute
{
    /**
     * The set of supported types
     */
    private final EnumSet<NodeType> supported;

    /**
     * The name of the format attribute
     */
    private final String fmt;

    /**
     * Protected constructor
     *
     * @param fmt the name for this format attribute
     * @param first first supported type
     * @param other other supported types, if any
     *
     * @see #supportedTypes()
     */
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

    /**
     * Return a new message for this format attribute
     *
     * @param data the validation context
     * @param message the message
     * @param <T> the type of the message
     * @return a new message
     * @see FormatMessages
     */
    protected final <T> ProcessingMessage newMsg(final FullData data,
        final T message)
    {
        return data.newMessage().put("domain", "validation")
            .put("keyword", "format").put("attribute", fmt).message(message)
            .put("value", data.getInstance().getNode());
    }
}
