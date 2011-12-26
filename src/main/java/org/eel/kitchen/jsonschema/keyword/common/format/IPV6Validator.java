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
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.keyword.common.format;

import com.google.common.net.InetAddresses;
import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;

import java.net.Inet6Address;

/**
 * Validator for the {@code ipv6} format specification
 *
 * <p>This uses {@link Inet6Address#getByName(String)} to validate,
 * which means we must ensure this is a "numerical" IPv6 address before
 * proceeding. Easy enough: {@code :} is not valid in host names,
 * but it required for IPv6 addresses...</p>
 */
public final class IPV6Validator
    extends FormatValidator
{
    private static final int IPV6_LENGTH = 16;

    @Override
    public ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
        throws JsonValidationFailureException
    {
        final ValidationReport report = context.createReport();

        final String ipaddr = instance.getTextValue();

        if (!InetAddresses.isInetAddress(ipaddr))
            report.fail("string is not a valid IPv6 address");

        if (InetAddresses.forString(ipaddr).getAddress().length != IPV6_LENGTH)
            report.fail("string is not a valid IPv6 address");

        return report;
    }
}
