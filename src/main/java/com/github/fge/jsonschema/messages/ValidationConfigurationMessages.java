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

public enum ValidationConfigurationMessages
{
    NULL_NAME("keyword name cannot be null"),
    NULL_SYNTAX_CHECKER("syntax checker must not be null"),
    NULL_DIGESTER("digester must not be null"),
    NULL_TYPE("type must not be null"),
    NO_APPROPRIATE_CONSTRUCTOR("validator class has no appropriate constructor"
        + " (expected a constructor with a JsonNode as an argument)"),
    NO_CHECKER("cannot build a keyword without a syntax checker"),
    MALFORMED_KEYWORD("when a validator class is defined, "
        + "a digester must also be present"),
    NULL_FORMAT("format attribute name cannot be null"),
    NULL_ATTRIBUTE("format attribute cannot be null"),
    NULL_LIBRARY("library cannot be null"),
    DUP_LIBRARY("a library already exists for this URI"),
    NULL_KEYWORD("keyword cannot be null"),
    ;

    private final String message;

    ValidationConfigurationMessages(final String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return message;
    }
}
