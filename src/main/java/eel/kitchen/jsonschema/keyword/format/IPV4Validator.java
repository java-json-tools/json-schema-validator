/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.keyword.format;

import eel.kitchen.jsonschema.ValidationReport;
import org.codehaus.jackson.JsonNode;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public final class IPV4Validator
    extends AbstractFormatValidator
{
    private static final Pattern pattern
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
            if (!pattern.matcher(ipaddr).matches())
                throw new UnknownHostException();
            Inet4Address.getByName(ipaddr);
        } catch (UnknownHostException ignored) {
            report.addMessage("string is not a valid IPv4 address");
        }

        return report;
    }
}
