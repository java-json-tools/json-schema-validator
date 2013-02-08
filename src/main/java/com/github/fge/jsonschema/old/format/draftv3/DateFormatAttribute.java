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

package com.github.fge.jsonschema.old.format.draftv3;

import com.github.fge.jsonschema.old.format.AbstractDateFormatAttribute;
import com.github.fge.jsonschema.old.format.FormatAttribute;

/**
 * Validator for the {@code date} format attribute.
 */
public final class DateFormatAttribute
    extends AbstractDateFormatAttribute
{
    private static final FormatAttribute INSTANCE = new DateFormatAttribute();

    public static FormatAttribute getInstance()
    {
        return INSTANCE;
    }

    private DateFormatAttribute()
    {
        super("yyyy-MM-dd", "date");
    }
}
