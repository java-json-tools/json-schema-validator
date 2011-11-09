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

package org.eel.kitchen.jsonschema.keyword.format;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.ValidationReport;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Specialized format validator for date/time checking
 *
 * <p>We use {@link SimpleDateFormat#parse(String)} for that, since it can
 * handle all defined formats.</p>
 */
public abstract class AbstractDateFormatValidator
    extends AbstractFormatValidator
{
    /**
     * The {@link SimpleDateFormat} to use
     */
    private final SimpleDateFormat format;

    /**
     * The error message in case of validation failure
     */
    private final String errmsg;

    /**
     * Constructor
     *
     * @param report The {@link org.eel.kitchen.jsonschema.ValidationReport} to use
     * @param node The node to validate
     * @param fmt The date format
     * @param desc the description of the date format
     */
    protected AbstractDateFormatValidator(final ValidationReport report,
        final JsonNode node, final String fmt, final String desc)
    {
        super(report, node);
        format = new SimpleDateFormat(fmt);
        errmsg = String.format("string is not a valid %s", desc);
    }

    /**
     * Validate the instance, using {@link SimpleDateFormat#parse(String)}
     *
     * @return the validation report
     */
    @Override
    public final ValidationReport validate()
    {
        try {
            format.parse(node.getTextValue());
        } catch (ParseException ignored) {
            report.addMessage(errmsg);
        }

        return report;
    }
}
