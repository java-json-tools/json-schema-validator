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

package com.github.fge.jsonschema.format.helpers;

import com.github.fge.jsonschema.format.AbstractFormatAttribute;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.NodeType;
import com.google.common.net.InetAddresses;

import static com.github.fge.jsonschema.messages.FormatMessages.*;

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
        final ValidationData data)
        throws ProcessingException
    {
        final String ipaddr = data.getInstance().getCurrentNode().textValue();

        if (InetAddresses.isInetAddress(ipaddr) && InetAddresses
            .forString(ipaddr).getAddress().length == IPV4_LENGTH)
            return;

        report.error(newMsg(data, INVALID_IPV4_ADDR));
    }
}
