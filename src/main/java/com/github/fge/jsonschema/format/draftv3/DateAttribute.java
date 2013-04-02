package com.github.fge.jsonschema.format.draftv3;

import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.format.helpers.AbstractDateFormatAttribute;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import static org.joda.time.DateTimeFieldType.*;

public final class DateAttribute
    extends AbstractDateFormatAttribute
{
    private static final FormatAttribute INSTANCE = new DateAttribute();

    private DateAttribute()
    {
        super("date", "yyyy-MM-dd");
    }

    public static FormatAttribute getInstance()
    {
        return INSTANCE;
    }

    @Override
    protected DateTimeFormatter getFormatter()
    {
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();

        builder = builder.appendFixedDecimal(year(), 4)
            .appendLiteral('-')
            .appendFixedDecimal(monthOfYear(), 2)
            .appendLiteral('-')
            .appendFixedDecimal(dayOfMonth(), 2);

        return builder.toFormatter();
    }
}
