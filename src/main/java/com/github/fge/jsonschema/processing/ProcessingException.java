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

public final class ProcessingException
    extends Exception
{
    private final ProcessingMessage processingMessage;

    public ProcessingException()
    {
        this(new ProcessingMessage());
    }

    public ProcessingException(final String message)
    {
        this(new ProcessingMessage().msg(message));
    }

    public ProcessingException(final ProcessingMessage processingMessage)
    {
        this.processingMessage = processingMessage
            .setLogLevel(LogLevel.FATAL);
    }

    @Override
    public String getMessage()
    {
        return processingMessage.toString();
    }

    @Override
    public String getLocalizedMessage()
    {
        return getMessage();
    }

    public ProcessingMessage getProcessingMessage()
    {
        return processingMessage;
    }
}
