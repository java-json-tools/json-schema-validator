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

import com.github.fge.jsonschema.core.keyword.syntax.dictionaries.DraftV4SyntaxCheckerDictionary;
import com.github.fge.jsonschema.library.digest.DraftV4DigesterDictionary;
import com.github.fge.jsonschema.library.format.DraftV4FormatAttributesDictionary;
import com.github.fge.jsonschema.library.validator.DraftV4ValidatorDictionary;

/**
 * Library of all draft v4 core schema keywords and format attributes
 */
public final class DraftV4Library
{
    private static final Library LIBRARY = new Library(
        DraftV4SyntaxCheckerDictionary.get(),
        DraftV4DigesterDictionary.get(),
        DraftV4ValidatorDictionary.get(),
        DraftV4FormatAttributesDictionary.get()
    );

    private DraftV4Library()
    {
    }

    public static Library get()
    {
        return LIBRARY;
    }
}
