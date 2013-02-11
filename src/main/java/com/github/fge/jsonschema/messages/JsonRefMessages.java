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

package com.github.fge.jsonschema.messages;

public enum JsonRefMessages
{
    ILLEGAL_POINTER("illegal JSON Pointer"),
    ILLEGAL_ESCAPE("bad escape seqeunce: '~' not followed by a valid token"),
    EMPTY_ESCAPE("bad escape sequence: '~' not followed by any token"),
    INVALID_URI("not a valid URI")
    ;
    private final String message;

    JsonRefMessages(final String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return message;
    }
}
