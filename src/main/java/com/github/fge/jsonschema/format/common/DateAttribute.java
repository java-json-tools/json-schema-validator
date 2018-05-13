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

import static org.joda.time.DateTimeFieldType.dayOfMonth;
import static org.joda.time.DateTimeFieldType.monthOfYear;
import static org.joda.time.DateTimeFieldType.year;

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

import java.util.List;

/**
 * Validator for the {@code date} format attribute
 */
public final class DateAttribute
    extends AbstractFormatAttribute
{
    private static final List<String> FORMATS = ImmutableList.of(
        "yyyy-MM-dd"
    );
    private static final DateTimeFormatter FORMATTER;

    static {

        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder()
                .appendFixedDecimal(year(), 4)
                .appendLiteral('-')
                .appendFixedDecimal(monthOfYear(), 2)
                .appendLiteral('-')
                .appendFixedDecimal(dayOfMonth(), 2);

        FORMATTER = builder.toFormatter();
    }

    private static final FormatAttribute INSTANCE = new DateAttribute();

    public static FormatAttribute getInstance()
    {
        return INSTANCE;
    }

    private DateAttribute()
    {
        super("date", NodeType.STRING);
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
