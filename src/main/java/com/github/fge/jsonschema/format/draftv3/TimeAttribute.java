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
