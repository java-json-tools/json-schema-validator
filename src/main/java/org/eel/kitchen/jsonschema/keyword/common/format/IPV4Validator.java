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

/**
 * Validator for the {@code ip-address} format specification, ie an IPv4 address
 *
 * <p>This uses Guava's {@link InetAddresses} to do the job.</p>
 */
public final class IPV4Validator
    extends FormatValidator
{
    private static final int IPV4_LENGTH = 4;

    @Override
    public ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
        throws JsonValidationFailureException
    {
        final ValidationReport report = context.createReport();

        final String ipaddr = instance.getTextValue();

        if (!InetAddresses.isInetAddress(ipaddr))
            report.fail("string is not a valid IPv4 address");

        if (InetAddresses.forString(ipaddr).getAddress().length != IPV4_LENGTH)
            report.fail("string is not a valid IPv4 address");

        return report;
    }
}
