/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.format;

import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;

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
     * @param key key in the format bundle message
     * @return a new message
     */
    protected final ProcessingMessage newMsg(final FullData data,
        final MessageBundle bundle, final String key)
    {
        return data.newMessage().put("domain", "validation")
            .put("keyword", "format").put("attribute", fmt)
            .setMessage(bundle.getMessage(key))
            .put("value", data.getInstance().getNode());
    }
}
