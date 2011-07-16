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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Validate a hostname. The problem here is that unlike for IP address
 * validation, where InetAddress' (and derivates) .getByName() only checks
 * for the well-formedness of the argument, the same method will try and
 * resolve the hostname. We don't want that.</p>
 *
 * <p>So, use regexes instead: split against the dot (strictly!),
 * and validate each part separately.</p>
 *
 * <p>Note that in a similar vein as for email addresses,
 * a hostname with no dots in it is VALID. This checker will therefore return
 * true with such an input.</p>
 */
public final class HostnameFormatValidator
    extends AbstractFormatValidator
{
    /**
     * <p>Regex to validate a hostname part. Source: Wikipedia, which thankfully
     * translated into layman terms what RFC 1123 says:</p>
     * <ul>
     *     <li>only ASCII digits and letters, plus the hyphen, are allowed,
     *     </li>
     *     <li>the first and last character must not be a hyphen,</li>
     *     <li>maximum length of 255 characters.</li>
     * </ul>
     * <p>Yes, I have to use a lazy quantifier, but I see no other way.</p>
     */
    private static final Pattern hostnamePart
        = Pattern.compile("^[a-z0-9][-a-z0-9]{0,253}?[a-z0-9]?$",
            Pattern.CASE_INSENSITIVE);

    @Override
    protected boolean doValidate(final JsonNode node)
    {
        final String value = node.getTextValue();
        final String[] parts = value.split("\\.");
        Matcher matcher;

        for (final String part: parts) {
            matcher = hostnamePart.matcher(part);
            if (!matcher.lookingAt()) {
                messages.add("string is not a valid hostname");
                return false;
            }
        }

        return true;
    }
}
