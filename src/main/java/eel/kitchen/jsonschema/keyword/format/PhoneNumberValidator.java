/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
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

/**
 * Attempt to validate the "phone" format specification
 *
 * <p>The draft says the phone may match E.123. The spec is quite large,
 * Wikipedia has a good summary of the spec. But validation could be vastly
 * improved.</p>
 */
//TODO: use Pattern
public final class PhoneNumberValidator
    extends AbstractFormatValidator
{
    public PhoneNumberValidator(final ValidationReport report,
        final JsonNode node)
    {
        super(report, node);
    }

    @Override
    public ValidationReport validate()
    {
        final String input = node.getTextValue();

        final String transformed = input.replaceFirst("^\\((\\d+)\\)", "\\1")
            .replaceFirst("^\\+", "")
            .replaceAll("-(?=\\d)", "")
            .replaceAll(" (?=\\d)", "")
            .replaceAll("\\d", "");

        if (!transformed.isEmpty())
            report.addMessage("string is not a recognized phone number");

        return report;
    }
}
