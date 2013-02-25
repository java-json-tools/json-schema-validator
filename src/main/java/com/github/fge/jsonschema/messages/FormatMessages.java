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

/**
 * Messages used by {@code format} keyword validation
 */
//TODO: reorganize
public enum FormatMessages
{
    FORMAT_NOT_SUPPORTED("format attribute not supported"),
    INVALID_DATE_FORMAT("input does not have the expected format"),
    INVALID_EMAIL("input is not a valid email address"),
    INVALID_HOSTNAME("input is not a valid hostname"),
    INVALID_IPV6_ADDR("input is not a valid IPv6 address"),
    INVALID_ECMA_262_REGEX("input is not a valid ECMA 262 regular expression"),
    INVALID_IPV4_ADDR("input is not a valid IPv4 address"),
    INVALID_PHONE_NUMBER("input is not recognized as a phone number"),
    EPOCH_NEGATIVE("epoch is negative"),
    EPOCH_OVERFLOW("possible epoch overflow (greater than 2^31 - 1 seconds)"),
    INVALID_URI("input is not a valid URI"),
    ;

    private final String message;

    FormatMessages(final String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return message;
    }
}
