/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
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

package org.eel.kitchen.jsonschema.main;

import org.eel.kitchen.jsonschema.report.Message;

/**
 * Generic exception thrown when the validation cannot proceed normally
 *
 * <p>Note that all constructors of this class take a {@link Message} as an
 * argument.</p>
 */
public final class JsonSchemaException
    extends Exception
{
    private final Message message;

    public JsonSchemaException(final Message message, final Exception e)
    {
        super(message.getMessage(), e);
        this.message = message;
    }

    public JsonSchemaException(final Message message)
    {
        this.message = message;
    }

    public Message getValidationMessage()
    {
        return message;
    }

    @Override
    public String getMessage()
    {
        return message.toString();
    }

    @Override
    public String getLocalizedMessage()
    {
        return message.toString();
    }

    @Override
    public String toString()
    {
        return getClass().getName() + ": " + message;
    }
}
