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

package com.github.fge.jsonschema.format.helpers;

import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.format.AbstractFormatAttribute;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.google.common.net.InetAddresses;

/**
 * Validator for both the {@code ip-address} (draft v3) and {@code ipv4} (draft
 * v4) format attributes.
 *
 * <p>This uses Guava's {@link InetAddresses} to do the job.</p>
 */
public final class IPv4FormatAttribute
    extends AbstractFormatAttribute
{
    private static final int IPV4_LENGTH = 4;

    public IPv4FormatAttribute(final String fmt)
    {
        super(fmt, NodeType.STRING);
    }

    @Override
    public void validate(final ProcessingReport report,
        final MessageBundle bundle, final FullData data)
        throws ProcessingException
    {
        final String ipaddr = data.getInstance().getNode().textValue();

        if (InetAddresses.isInetAddress(ipaddr) && InetAddresses
            .forString(ipaddr).getAddress().length == IPV4_LENGTH)
            return;

        report.error(newMsg(data, bundle, "err.format.invalidIPv4Address")
            .putArgument("value", ipaddr));
    }
}
