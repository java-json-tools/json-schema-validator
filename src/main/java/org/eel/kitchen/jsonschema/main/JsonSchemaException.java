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

import org.eel.kitchen.jsonschema.keyword.KeywordValidator;

/**
 * Generic exception thrown when the validation cannot proceed normally
 *
 * <p>This exception is actually never thrown by "client" methods:
 * internally, they are used to build schemas/validators which always fails.
 * This can happen, for instance, if:</p>
 *
 * <ul>
 *     <li>{@code $ref} resolution fails,</li>
 *     <li>a {@link KeywordValidator} cannot be built,</li>
 *     <li>other</li>
 * </ul>
 */
public final class JsonSchemaException
    extends Exception
{
    public JsonSchemaException(final String message)
    {
        super(message);
    }

    public JsonSchemaException(final String message, final Exception e)
    {
        super(message, e);
    }
}
