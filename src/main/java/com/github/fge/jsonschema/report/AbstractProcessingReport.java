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

import com.github.fge.jsonschema.exceptions.ProcessingException;

public abstract class AbstractProcessingReport
    implements ProcessingReport
{
    protected LogLevel currentLevel = LogLevel.DEBUG;
    private LogLevel logLevel = LogLevel.INFO;
    private LogLevel exceptionThreshold = LogLevel.FATAL;

    protected AbstractProcessingReport()
    {
    }

    @Override
    public final void setLogLevel(final LogLevel level)
    {
        logLevel = level;
    }

    @Override
    public final void setExceptionThreshold(final LogLevel level)
    {
        exceptionThreshold = level;
    }

    @Override
    public final LogLevel getLogLevel()
    {
        return logLevel;
    }

    @Override
    public final LogLevel getExceptionThreshold()
    {
        return exceptionThreshold;
    }

    @Override
    public final void debug(final ProcessingMessage message)
        throws ProcessingException
    {
        log(message.setLogLevel(LogLevel.DEBUG));
    }

    @Override
    public final void info(final ProcessingMessage message)
        throws ProcessingException
    {
        log(message.setLogLevel(LogLevel.INFO));
    }

    @Override
    public final void warn(final ProcessingMessage message)
        throws ProcessingException
    {
        log(message.setLogLevel(LogLevel.WARNING));
    }

    @Override
    public final void error(final ProcessingMessage message)
        throws ProcessingException
    {
        log(message.setLogLevel(LogLevel.ERROR));
    }

    @Override
    public final boolean isSuccess()
    {
        return currentLevel.compareTo(LogLevel.ERROR) < 0;
    }

    public abstract void doLog(final ProcessingMessage message);

    @Override
    public final void log(final ProcessingMessage message)
        throws ProcessingException
    {
        final LogLevel level = message.getLogLevel();

        if (level.compareTo(exceptionThreshold) >= 0)
            throw message.asException();
        if (level.compareTo(currentLevel) > 0)
            currentLevel = level;
        if (level.compareTo(logLevel) >= 0)
            doLog(message.setLogLevel(level));
    }

    @Override
    public final ProcessingMessage newMessage()
    {
        return new ProcessingMessage();
    }
}
