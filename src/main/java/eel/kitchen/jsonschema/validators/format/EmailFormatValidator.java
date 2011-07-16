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

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Validate an email address. Note that the spec does not specify (nor RFC
 * 822 dictates) that the email should be a "fully qualified" email,
 * ie with a right part after a @. Such emails with no right part ARE valid,
 * and that's the end of it.
 */
public final class EmailFormatValidator
    extends AbstractFormatValidator
{
    /**
     * Validates this instance by calling javax.mail.internet's
     * InternetAddress() on the input, which fits the bill perfectly.
     *
     * @param node the instance to validate
     * @return true if the instance is valid
     */
    @Override
    protected boolean doValidate(final JsonNode node)
    {
        try {
            new InternetAddress(node.getTextValue());
            return true;
        } catch (AddressException e) {
            messages.add("string is not a valid email address");
            return false;
        }
    }
}
