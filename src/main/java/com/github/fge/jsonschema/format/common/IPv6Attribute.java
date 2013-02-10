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

package com.github.fge.jsonschema.format.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.format.AbstractFormatAttribute;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.NodeType;
import com.google.common.net.InetAddresses;

import static com.github.fge.jsonschema.messages.FormatMessages.*;

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
        final ValidationData data)
        throws ProcessingException
    {
        final JsonNode instance = data.getInstance().getNode();
        final String ipaddr = instance.textValue();

        if (InetAddresses.isInetAddress(ipaddr) && InetAddresses
            .forString(ipaddr).getAddress().length == IPV6_LENGTH)
            return;

        report.error(newMsg(data, INVALID_IPV6_ADDR));
    }
}
