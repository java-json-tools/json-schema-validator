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

package com.github.fge.jsonschema.processing;

import com.google.common.annotations.VisibleForTesting;

public abstract class ProcessingContext<T>
{
    protected LogThreshold currentThreshold = LogThreshold.DEBUG;
    protected LogThreshold logThreshold = LogThreshold.INFO;
    protected LogThreshold exceptionThreshold = LogThreshold.FATAL;

    public final void setLogThreshold(final LogThreshold threshold)
    {
        logThreshold = threshold;
    }

    public final void setExceptionThreshold(final LogThreshold threshold)
    {
        exceptionThreshold = threshold;
    }

    public final void debug(final ProcessingMessage msg)
        throws ProcessingException
    {
        doLog(LogThreshold.DEBUG, msg);
    }

    public final void info(final ProcessingMessage msg)
        throws ProcessingException
    {
        doLog(LogThreshold.INFO, msg);
    }

    public final void warn(final ProcessingMessage msg)
        throws ProcessingException
    {
        doLog(LogThreshold.WARNING, msg);
    }

    public final void error(final ProcessingMessage msg)
        throws ProcessingException
    {
        doLog(LogThreshold.ERROR, msg);
    }

    public final void fatal(final ProcessingMessage msg)
        throws ProcessingException
    {
        doLog(LogThreshold.FATAL, msg);
    }

    public final boolean isSuccess()
    {
        return currentThreshold.compareTo(LogThreshold.ERROR) < 0;
    }

    public abstract void log(final ProcessingMessage msg);

    public abstract ProcessingException buildException(
        final ProcessingMessage msg);

    @VisibleForTesting
    final void doLog(final LogThreshold threshold, final ProcessingMessage msg)
        throws ProcessingException
    {
        if (threshold.compareTo(exceptionThreshold) >= 0)
            throw buildException(msg);
        if (threshold.compareTo(currentThreshold) > 0)
            currentThreshold = threshold;
        if (threshold.compareTo(logThreshold) >= 0)
            log(msg.setLogThreshold(threshold));
    }

    public abstract ProcessingMessage newMessage();

    public abstract T getOutput();
}
