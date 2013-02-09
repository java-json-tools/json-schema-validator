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

package com.github.fge.jsonschema.library;

import com.github.fge.jsonschema.library.digest.DraftV3DigesterDictionary;
import com.github.fge.jsonschema.library.format.DraftV3FormatAttributesDictionary;
import com.github.fge.jsonschema.library.syntax.DraftV3SyntaxCheckerDictionary;
import com.github.fge.jsonschema.library.validator.DraftV3ValidatorDictionary;

public final class DraftV3Library
{
    private static final Library LIBRARY = new Library(
        DraftV3SyntaxCheckerDictionary.get(),
        DraftV3DigesterDictionary.get(),
        DraftV3ValidatorDictionary.get(),
        DraftV3FormatAttributesDictionary.get()
    );

    private DraftV3Library()
    {
    }

    public static Library get()
    {
        return LIBRARY;
    }
}
