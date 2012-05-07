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

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;

/**
 * Specialized format validator for date/time checking
 *
 * <p><a href="http://joda-time.sourceforge.net/">Joda</a> is used for date and
 * time parsing, and more specifically
 * {@link DateTimeFormatter#parseDateTime(String)}: it can handle all defined
 * formats, and catches more errors than {@link SimpleDateFormat} does.</p>
 */
public abstract class GenericDateFormatValidator
    extends FormatValidator
{
    /**
     * The error message in case of validation failure
     */
    private final String errmsg;

    /**
     * The {@link DateTimeFormatter} to use
     */
    private final DateTimeFormatter dtf;

    /**
     * Constructor
     *
     * @param fmt The date format
     * @param desc the description of the date format
     */
    protected GenericDateFormatValidator(final String fmt, final String desc)
    {
        dtf = DateTimeFormat.forPattern(fmt);
        errmsg = String.format("string is not a valid %s", desc);
    }

    @Override
    public final ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
    {
        final ValidationReport report = context.createReport();

        try {
            dtf.parseDateTime(instance.textValue());
        } catch (IllegalArgumentException ignored) {
            report.message(errmsg);
        }

        return report;
    }
}
