package com.github.fge.jsonschema.format.common;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.List;

import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.format.AbstractFormatAttribute;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.google.common.collect.ImmutableList;

/**
 * A {@link DateTimeFormatter} for date and time format defined in RFC3339.  
 * @see <a href="https://tools.ietf.org/html/rfc3339#section-5.6">RFC 3339 - Section 5.6</a>
 */
public class RFC3339DateTimeAttribute extends AbstractFormatAttribute {

	private static final List<String> RFC3339_FORMATS = ImmutableList.of(
	        "yyyy-MM-dd'T'HH:mm:ss((+|-)HH:mm|Z)", "yyyy-MM-dd'T'HH:mm:ss.[0-9]{1,9}((+|-)HH:mm|Z)"
	    );
	
    private static final DateTimeFormatter RFC3339_FORMATTER;

    static {
        final DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd")
                .appendLiteral('T')
                .appendPattern("HH:mm:ss")
                .optionalStart()
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
                .optionalEnd()
                .appendOffset("+HH:mm", "Z");
        RFC3339_FORMATTER = builder.toFormatter();
    }

    private static final FormatAttribute INSTANCE = new RFC3339DateTimeAttribute();

    public static FormatAttribute getInstance()
    {
        return INSTANCE;
    }

    private RFC3339DateTimeAttribute()
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
            RFC3339_FORMATTER.parse(value);
        } catch (DateTimeParseException ignored) {
            report.error(newMsg(data, bundle, "err.format.invalidDate")
                .putArgument("value", value).putArgument("expected", RFC3339_FORMATS));
        }
    }
}
