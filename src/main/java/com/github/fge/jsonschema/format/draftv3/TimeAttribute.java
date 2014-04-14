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

package com.github.fge.jsonschema.format.draftv3;

import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.format.helpers.AbstractDateFormatAttribute;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import static org.joda.time.DateTimeFieldType.*;

public final class TimeAttribute
    extends AbstractDateFormatAttribute
{
    private static final FormatAttribute INSTANCE = new TimeAttribute();

    private TimeAttribute()
    {
        super("time", "HH:mm:ss");
    }

    public static FormatAttribute getInstance()
    {
        return INSTANCE;
    }

    @Override
    protected DateTimeFormatter getFormatter()
    {
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();

        builder = builder.appendFixedDecimal(hourOfDay(), 2)
            .appendLiteral(':')
            .appendFixedDecimal(minuteOfHour(), 2)
            .appendLiteral(':')
            .appendFixedDecimal(secondOfMinute(), 2);

        return builder.toFormatter();
    }
}
