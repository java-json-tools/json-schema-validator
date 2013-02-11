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

import java.util.List;

public interface ProcessingReport
    extends MessageProvider
{
    void setLogLevel(LogLevel level);

    void setExceptionThreshold(LogLevel level);

    LogLevel getLogLevel();

    LogLevel getExceptionThreshold();

    void log(ProcessingMessage message)
        throws ProcessingException;

    void debug(ProcessingMessage message)
        throws ProcessingException;

    void info(ProcessingMessage message)
        throws ProcessingException;

    void warn(ProcessingMessage message)
        throws ProcessingException;

    void error(ProcessingMessage message)
        throws ProcessingException;

    boolean isSuccess();

    List<ProcessingMessage> getMessages();
}
