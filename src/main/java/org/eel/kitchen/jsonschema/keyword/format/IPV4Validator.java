/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.keyword.format;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.ValidationReport;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

/**
 * Validator for the "ip-address" format specification, ie an IPv4 address
 *
 * <p>This uses {@link Inet4Address#getByName(String)} to validate,
 * but this means we must ensure the shape of the address is good first: when
 * given a hostname, it will try to resolve it and we don't want that.</p>
 */
public final class IPV4Validator
    extends AbstractFormatValidator
{
    /**
     * Pattern to recognize a numerical IPv4 address
     */
    private static final Pattern IPV4_ADDR
        = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");

    public IPV4Validator(final ValidationReport report, final JsonNode node)
    {
        super(report, node);
    }

    @Override
    public ValidationReport validate()
    {
        try {
            final String ipaddr = node.getTextValue();
            if (!IPV4_ADDR.matcher(ipaddr).matches())
                throw new UnknownHostException();
            Inet4Address.getByName(ipaddr);
        } catch (UnknownHostException ignored) {
            report.addMessage("string is not a valid IPv4 address");
        }

        return report;
    }
}
