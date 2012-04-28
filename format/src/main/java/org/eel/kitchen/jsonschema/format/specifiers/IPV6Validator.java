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

package org.eel.kitchen.jsonschema.format.specifiers;

import com.google.common.net.InetAddresses;
import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;

/**
 * Validator for the {@code ipv6} format specification
 *
 * <p>This uses Guava's {@link InetAddresses} to do the job.</p>
 */
public final class IPV6Validator
    extends FormatValidator
{
    private static final int IPV6_LENGTH = 16;

    @Override
    public ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
    {
        final ValidationReport report = context.createReport();

        final String ipaddr = instance.textValue();

        if (!InetAddresses.isInetAddress(ipaddr)) {
            report.message("string is not a valid IPv6 address");
            return report;
        }

        if (InetAddresses.forString(ipaddr).getAddress().length != IPV6_LENGTH)
            report.message("string is not a valid IPv6 address");

        return report;
    }
}
