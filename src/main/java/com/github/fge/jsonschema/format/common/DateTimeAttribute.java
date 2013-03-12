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

import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.format.AbstractFormatAttribute;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.messages.FormatMessages;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.NodeType;
import com.google.common.collect.ImmutableList;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Validator for the {@code date-time} format attribute
 */
public final class DateTimeAttribute
    extends AbstractFormatAttribute
{
    private static final String FORMAT1 = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static final DateTimeFormatter FMT1
        = DateTimeFormat.forPattern(FORMAT1);
    private static final String FORMAT2 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final DateTimeFormatter FMT2
        = DateTimeFormat.forPattern(FORMAT2);
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
            FMT1.parseDateTime(value);
            return;
        } catch (IllegalArgumentException ignored) {
        }

        try {
            FMT2.parseDateTime(value);
            return;
        } catch (IllegalArgumentException ignored) {
        }

        report.error(newMsg(data, FormatMessages.INVALID_DATE_FORMAT)
            .put("expected", ImmutableList.of(FORMAT1, FORMAT2)));
    }
}
