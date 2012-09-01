/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
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

package org.eel.kitchen.jsonschema.format;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.report.ValidationMessage;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.validator.ValidationContext;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;

/**
 * Specialized format validator for date/time checking
 *
 * <p><a href="http://joda-time.sourceforge.net/">Joda Time</a> is used for
 * date and time parsing: it can handle all defined formats, and catches more
 * errors than the standard JDK's {@link SimpleDateFormat} does.</p>
 *
 * <p>What's more, unlike {@link SimpleDateFormat}, Joda Time's
 * {@link DateTimeFormatter} is thread-safe!</p>
 */
public abstract class AbstractDateFormatSpecifier
    extends FormatSpecifier
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
    protected AbstractDateFormatSpecifier(final String fmt, final String desc)
    {
        super(NodeType.STRING);
        dtf = DateTimeFormat.forPattern(fmt);
        errmsg = "string is not a valid " + desc;
    }

    @Override
    public final void checkValue(final String fmt, final ValidationContext ctx,
        final ValidationReport report, final JsonNode instance)
    {
        try {
            dtf.parseDateTime(instance.textValue());
        } catch (IllegalArgumentException ignored) {
            final ValidationMessage.Builder msg = newMsg(fmt)
                .setMessage(errmsg).addInfo("value", instance);
            report.addMessage(msg.build());
        }
    }
}
