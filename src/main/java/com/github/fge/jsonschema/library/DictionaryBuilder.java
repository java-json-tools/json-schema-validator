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

import com.google.common.collect.Maps;

import java.util.Map;

public final class DictionaryBuilder<T>
{
    final Map<String, T> entries = Maps.newHashMap();

    DictionaryBuilder()
    {
    }

    public DictionaryBuilder<T> addEntry(final String key, final T value)
    {
        entries.put(key, value);
        return this;
    }

    public Dictionary<T> build()
    {
        return new Dictionary<T>(this);
    }
}
