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

package com.github.fge.jsonschema.format.helpers;

import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.format.AbstractFormatAttribute;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;

/**
 * Abstract class for date/time related format attributes
 *
 * <p><a href="http://joda-time.sourceforge.net/">Joda Time</a> is used for
 * date and time parsing: it can handle all defined formats, and catches more
 * errors than the standard JDK's {@link SimpleDateFormat} does.</p>
 *
 * <p>Furthermore (and more importantly), unlike {@link SimpleDateFormat}, Joda
 * Time's {@link DateTimeFormatter} is thread-safe!</p>
 */
public abstract class AbstractDateFormatAttribute
    extends AbstractFormatAttribute
{
    private final String format;

    protected AbstractDateFormatAttribute(final String fmt, final String format)
    {
        super(fmt, NodeType.STRING);
        this.format = format;
    }

    protected abstract DateTimeFormatter getFormatter();

    @Override
    public final void validate(final ProcessingReport report,
        final MessageBundle bundle, final FullData data)
        throws ProcessingException
    {
        final DateTimeFormatter formatter = getFormatter();
        final String value = data.getInstance().getNode().textValue();

        try {
            formatter.parseDateTime(value);
        } catch (IllegalArgumentException ignored) {
            report.error(newMsg(data, bundle, "err.format.invalidDate")
                .putArgument("value", value).putArgument("expected", format));
        }
    }
}
