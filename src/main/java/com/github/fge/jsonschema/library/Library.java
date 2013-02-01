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

import com.github.fge.jsonschema.syntax.SyntaxChecker;
import com.github.fge.jsonschema.util.Frozen;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

public final class Library
    implements Frozen<MutableLibrary>
{
    final Map<String, SyntaxChecker> syntaxCheckers;

    Library(final MutableLibrary mutableLibrary)
    {
        syntaxCheckers = ImmutableMap.copyOf(mutableLibrary.syntaxCheckers);
    }

    @Override
    public MutableLibrary thaw()
    {
        return new MutableLibrary(this);
    }
}
