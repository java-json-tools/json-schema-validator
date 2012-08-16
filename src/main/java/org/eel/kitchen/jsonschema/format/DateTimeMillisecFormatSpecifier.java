/*
 * Copyright (c) 2012, Corey Sciuto <csciuto@constantcontact.com>
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

package org.eel.kitchen.jsonschema.format;

/**
 * Validator for the custom {@code date-time-ms} format specification
 */
public final class DateTimeMillisecFormatSpecifier
    extends AbstractDateFormatSpecifier
{
    private static final FormatSpecifier instance
        = new DateTimeMillisecFormatSpecifier();

    public static FormatSpecifier getInstance()
    {
        return instance;
    }

    private DateTimeMillisecFormatSpecifier()
    {
        super("yyyy-MM-dd'T'HH:mm:ss.SSSZ", "ISO 8601 date (with milliseconds)");
    }
}
