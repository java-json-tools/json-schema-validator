/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
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

package org.eel.kitchen.jsonschema.format;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.InetAddresses;
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.NodeType;

/**
 * Validator for both the {@code ip-address} (draft v3) and {@code ipv4} (draft
 * v4) format attributes.
 *
 * <p>This uses Guava's {@link InetAddresses} to do the job.</p>
 */
public final class IPV4FormatAttribute
    extends FormatAttribute
{
    private static final FormatAttribute instance = new IPV4FormatAttribute();

    private static final int IPV4_LENGTH = 4;

    private IPV4FormatAttribute()
    {
        super(NodeType.STRING);
    }

    public static FormatAttribute getInstance()
    {
        return instance;
    }

    @Override
    public void checkValue(final String fmt, final ValidationReport report,
        final JsonNode value)
    {
        final String ipaddr = value.textValue();

        if (InetAddresses.isInetAddress(ipaddr) && InetAddresses
            .forString(ipaddr).getAddress().length == IPV4_LENGTH)
            return;

        final Message.Builder msg = newMsg(fmt).addInfo("value", value)
            .setMessage("string is not a valid IPv4 address");
        report.addMessage(msg.build());
    }
}
