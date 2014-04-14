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

package com.github.fge.jsonschema.format.extra;

import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.format.AbstractFormatAttribute;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;

import java.util.UUID;

/**
 * Format specifier for a proposed {@code uuid} attribute
 *
 * @see UUID#fromString(String)
 */
public final class UUIDFormatAttribute
    extends AbstractFormatAttribute
{
    private static final FormatAttribute instance = new UUIDFormatAttribute();

    private UUIDFormatAttribute()
    {
        super("uuid", NodeType.STRING);
    }

    public static FormatAttribute getInstance()
    {
        return instance;
    }

    @Override
    public void validate(final ProcessingReport report,
        final MessageBundle bundle, final FullData data)
        throws ProcessingException
    {
        final String input = data.getInstance().getNode().textValue();

        try {
            UUID.fromString(input);
        } catch (IllegalArgumentException ignored) {
            report.error(newMsg(data, bundle, "err.format.UUID.invalid")
                .putArgument("value", input));
        }
    }
}
