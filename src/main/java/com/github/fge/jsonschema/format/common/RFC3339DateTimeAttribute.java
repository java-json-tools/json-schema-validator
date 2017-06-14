package com.github.fge.jsonschema.format.common;

import java.util.List;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

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
	        "yyyy-MM-dd'T'HH:mm:ss((+|-)HH:mm|Z)", "yyyy-MM-dd'T'HH:mm:ss.[0-9]{1,12}((+|-)HH:mm|Z)"
	    );
	
    private static final DateTimeFormatter FORMATTER;

    static {
        final DateTimeParser secFracsParser = new DateTimeFormatterBuilder()
                .appendLiteral('.').appendFractionOfSecond(1,12)
                .toParser();

        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                .appendOptional(secFracsParser)
                .appendTimeZoneOffset("Z", true, 2, 2);

        FORMATTER = builder.toFormatter();
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

        try 
        {
            FORMATTER.parseDateTime(value);

            final String secFracsAndOffset = value.substring("yyyy-MM-ddTHH:mm:ss".length());
            final String offset;
            if (!secFracsAndOffset.startsWith(".")) {
            	offset = secFracsAndOffset;
            } else{
            	if  (secFracsAndOffset.contains("Z")) {
            		offset = secFracsAndOffset.substring(secFracsAndOffset.indexOf("Z"));
            	} else if (secFracsAndOffset.contains("+")) {
            		offset = secFracsAndOffset.substring(secFracsAndOffset.indexOf("+"));
            	} else { 
            		offset = secFracsAndOffset.substring(secFracsAndOffset.indexOf("-"));
            	}
            }
            if (!isOffSetStrictRFC3339(offset)) {
            	throw new IllegalArgumentException();
            }
            
        } catch (IllegalArgumentException ignored) {
    		report.error(newMsg(data, bundle, "err.format.invalidDate")
			    .putArgument("value", value).putArgument("expected", RFC3339_FORMATS));
        }
        
    }

    /**
     * Return true if date-time offset stricly follows RFC3339:
     * <code>time-hour       = 2DIGIT  ; 00-23</code>
     * <code>time-minute     = 2DIGIT  ; 00-59</code>
     * <code>time-numoffset  = ("+" / "-") time-hour ":" time-minute</code>
     * <code>time-offset     = "Z" / time-numoffset</code>,
     * and false otherwise
     * @param offset
     * @return
     */
    private boolean isOffSetStrictRFC3339(final String offset) 
    {
		if (offset.endsWith("Z")) return true;
		if (offset.length() == 6 && offset.contains(":")) {
			return true;
		}
		return false;
	}
}
