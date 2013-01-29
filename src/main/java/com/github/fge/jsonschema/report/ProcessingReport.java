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

package com.github.fge.jsonschema.report;

import com.github.fge.jsonschema.processing.LogLevel;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.google.common.annotations.VisibleForTesting;

public abstract class ProcessingReport
{
    protected LogLevel currentLevel = LogLevel.DEBUG;
    protected LogLevel logLevel = LogLevel.INFO;
    protected LogLevel exceptionThreshold = LogLevel.FATAL;

    public final void setLogLevel(final LogLevel threshold)
    {
        logLevel = threshold;
    }

    public final void setExceptionThreshold(final LogLevel threshold)
    {
        exceptionThreshold = threshold;
    }

    public final void debug(final ProcessingMessage msg)
        throws ProcessingException
    {
        doLog(LogLevel.DEBUG, msg);
    }

    public final void info(final ProcessingMessage msg)
        throws ProcessingException
    {
        doLog(LogLevel.INFO, msg);
    }

    public final void warn(final ProcessingMessage msg)
        throws ProcessingException
    {
        doLog(LogLevel.WARNING, msg);
    }

    public final void error(final ProcessingMessage msg)
        throws ProcessingException
    {
        doLog(LogLevel.ERROR, msg);
    }

    public final void fatal(final ProcessingMessage msg)
        throws ProcessingException
    {
        doLog(LogLevel.FATAL, msg);
    }

    public final boolean isSuccess()
    {
        return currentLevel.compareTo(LogLevel.ERROR) < 0;
    }

    public abstract void log(final ProcessingMessage msg);

    @VisibleForTesting
    final void doLog(final LogLevel level, final ProcessingMessage msg)
        throws ProcessingException
    {
        if (level.compareTo(exceptionThreshold) >= 0)
            throw new ProcessingException(msg);
        if (level.compareTo(currentLevel) > 0)
            currentLevel = level;
        if (level.compareTo(logLevel) >= 0)
            log(msg.setLogLevel(level));
    }
}
