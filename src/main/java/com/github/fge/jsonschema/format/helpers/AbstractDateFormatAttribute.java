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

package com.github.fge.jsonschema.format.helpers;

import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.format.AbstractFormatAttribute;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.report.ProcessingReport;
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
            report.error(newMsg(data, bundle, "err.common.invalidDate")
                .putArgument("value", value).putArgument("expected", format));
        }
    }
}
