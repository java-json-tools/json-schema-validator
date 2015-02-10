/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.format.common;

import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.format.AbstractFormatAttribute;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;
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
        "yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd'T'HH:mm:ss.[0-9]{1,12}Z"
    );
    private static final DateTimeFormatter FORMATTER;

    static {
        final DateTimeParser secFracsParser = new DateTimeFormatterBuilder()
            .appendLiteral('.').appendFractionOfSecond(1,12)
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
            .appendOptional(secFracsParser)
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
    public void validate(final ProcessingReport report,
        final MessageBundle bundle, final FullData data)
        throws ProcessingException
    {
        final String value = data.getInstance().getNode().textValue();

        try {
            FORMATTER.parseDateTime(value);
        } catch (IllegalArgumentException ignored) {
            report.error(newMsg(data, bundle, "err.format.invalidDate")
                .putArgument("value", value).putArgument("expected", FORMATS));
        }
    }
}
