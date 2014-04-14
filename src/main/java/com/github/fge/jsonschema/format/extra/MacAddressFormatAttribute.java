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

import java.util.regex.Pattern;

/**
 * Hypothetical format attribute for {@code mac}
 *
 * <p>This specifier will check if a string instance is a valid MAC address.</p>
 */
public final class MacAddressFormatAttribute
    extends AbstractFormatAttribute
{
    private static final FormatAttribute instance
        = new MacAddressFormatAttribute();

    // Yep, a regex...
    private static final Pattern MACADDR
        = Pattern.compile("[A-Za-z0-9]{2}(?::[A-Za-z0-9]{2}){5}");

    public static FormatAttribute getInstance()
    {
        return instance;
    }

    private MacAddressFormatAttribute()
    {
        super("mac", NodeType.STRING);
    }

    @Override
    public void validate(final ProcessingReport report,
        final MessageBundle bundle, final FullData data)
        throws ProcessingException
    {
        final String input = data.getInstance().getNode().textValue();

        if (!MACADDR.matcher(input).matches())
            report.error(newMsg(data, bundle, "err.format.macAddr.invalid")
                .putArgument("value", input));
    }
}
