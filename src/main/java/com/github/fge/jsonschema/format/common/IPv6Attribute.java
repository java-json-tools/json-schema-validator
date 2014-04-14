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

package com.github.fge.jsonschema.format.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.format.AbstractFormatAttribute;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.google.common.net.InetAddresses;

/**
 * Validator for the {@code ipv6} format attribute.
 *
 * <p>This uses Guava's {@link InetAddresses} to do the job.</p>
 */
public final class IPv6Attribute
    extends AbstractFormatAttribute
{
    private static final int IPV6_LENGTH = 16;

    private static final FormatAttribute INSTANCE = new IPv6Attribute();

    public static FormatAttribute getInstance()
    {
        return INSTANCE;
    }

    private IPv6Attribute()
    {
        super("ipv6", NodeType.STRING);
    }

    @Override
    public void validate(final ProcessingReport report,
        final MessageBundle bundle, final FullData data)
        throws ProcessingException
    {
        final JsonNode instance = data.getInstance().getNode();
        final String ipaddr = instance.textValue();

        if (InetAddresses.isInetAddress(ipaddr) && InetAddresses
            .forString(ipaddr).getAddress().length == IPV6_LENGTH)
            return;

        report.error(newMsg(data, bundle, "err.format.invalidIPV6Address")
            .putArgument("value", ipaddr));
    }
}
