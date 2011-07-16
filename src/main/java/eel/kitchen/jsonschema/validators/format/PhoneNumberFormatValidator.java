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

/**
 * <p>Validate a phone number. Well, try to validate one, that is. The standard
 * says it may match E.123, but for the heck of me I cannot find it explained
 * in layman terms, so these are the shapes recognized by this validator:
 * </p>
 * <ul>
 *     <li>(xxx) xxx-xx xx-...</li>
 *     <li>+xxx xxx xx xx</li>
 * </ul>
 * <p>Also, there's no E.123 format validation Java library that I know of.</p>
 */
public final class PhoneNumberFormatValidator
    extends AbstractFormatValidator
{
    @Override
    protected boolean doValidate(final JsonNode node)
    {
        final String input = node.getTextValue();

        final String transformed = input.replaceFirst("^\\((\\d+)\\)", "\\1")
            .replaceFirst("^\\+", "")
            .replaceAll("-(?=\\d)", "")
            .replaceAll(" (?=\\d)", "")
            .replaceAll("\\d", "");

        if (transformed.isEmpty())
            return true;

        messages.add("string is not a recognized phone number");
        return false;
    }
}
