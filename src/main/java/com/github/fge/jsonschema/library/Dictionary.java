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

import com.github.fge.jsonschema.util.Frozen;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/*
 * Wrapper class over a Map<String, T>
 *
 * Use it to collect what exists as strings, entries etc.
 */
public final class Dictionary<T>
    implements Frozen<DictionaryBuilder<T>>
{
    final Map<String, T> entries;

    public static <T> DictionaryBuilder<T> newBuilder()
    {
        return new DictionaryBuilder<T>();
    }

    Dictionary(final DictionaryBuilder<T> builder)
    {
        entries = ImmutableMap.copyOf(builder.entries);
    }

    public Map<String, T> entries()
    {
        return entries;
    }

    @Override
    public DictionaryBuilder<T> thaw()
    {
        return new DictionaryBuilder<T>(this);
    }
}
