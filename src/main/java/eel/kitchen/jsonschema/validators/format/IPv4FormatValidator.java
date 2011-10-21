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

package eel.kitchen.jsonschema.validators.format;

import org.codehaus.jackson.JsonNode;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

/**
 * Validate an IPv4 address. Java has <code>Inet4Address.getByName()</code>:
 * when passed a literal IP adddress, it will just check for the validity of it.
 *
 * <p>But first we need to know whether this is actually an IP address, since
 * this same operation will try to resolve a hostname if it is passed a
 * hostname.</p>
 */

// TODO: write more tests

public final class IPv4FormatValidator
    extends AbstractFormatValidator
{
    /**
     * Pattern to recognize a dotted quad number
     */
    private static final Pattern pattern
        = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");

    @Override
    protected boolean doValidate(final JsonNode node)
    {
        try {
            final String ipaddr = node.getTextValue();
            if (!pattern.matcher(ipaddr).matches())
                throw new UnknownHostException();
            Inet4Address.getByName(ipaddr);
            return true;
        } catch (UnknownHostException ignored) {
            messages.add("string is not a valid IPv4 address");
            return false;
        }
    }
}
