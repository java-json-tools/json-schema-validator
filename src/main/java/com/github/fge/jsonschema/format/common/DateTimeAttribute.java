/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.jsonschema.format.common;

import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.format.AbstractFormatAttribute;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.google.common.collect.ImmutableList;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

import java.util.List;

import static org.joda.time.DateTimeFieldType.*;

/**
 * Validator for the {@code date-time} format attribute
 */
public final class DateTimeAttribute
    extends AbstractFormatAttribute
{
    private static final List<String> FORMATS = ImmutableList.of(
        "yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    );
    private static final DateTimeFormatter FORMATTER;

    static {
        final DateTimeParser msParser = new DateTimeFormatterBuilder()
            .appendLiteral('.').appendDecimal(millisOfSecond(), 1, 3)
            .toParser();

        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();

        builder = builder.appendFixedDecimal(year(), 4)
            .appendLiteral('-')
            .appendFixedDecimal(monthOfYear(), 2)
            .appendLiteral('-')
            .appendFixedDecimal(dayOfMonth(), 2)
            .appendLiteral('T')
            .appendFixedDecimal(hourOfDay(), 2)
            .appendLiteral(':')
            .appendFixedDecimal(minuteOfHour(), 2)
            .appendLiteral(':')
            .appendFixedDecimal(secondOfMinute(), 2)
            .appendOptional(msParser)
            .appendTimeZoneOffset("Z", false, 2, 2);

        FORMATTER = builder.toFormatter();
    }

    private static final FormatAttribute INSTANCE = new DateTimeAttribute();

    public static FormatAttribute getInstance()
    {
        return INSTANCE;
    }

    private DateTimeAttribute()
    {
        super("date-time", NodeType.STRING);
    }

    @Override
    public void validate(final ProcessingReport report, final FullData data)
        throws ProcessingException
    {
        final String value = data.getInstance().getNode().textValue();

        try {
            FORMATTER.parseDateTime(value);
        } catch (IllegalArgumentException ignored) {
            report.error(newMsg(data, "invalidDateFormat")
                .put("expected", FORMATS));
        }
    }
}
