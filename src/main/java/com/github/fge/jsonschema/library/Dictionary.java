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
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

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

    @VisibleForTesting
    public T get(final String key)
    {
        return entries.get(key);
    }

    public Set<String> missingEntriesFrom(final Set<String> set)
    {
        final Set<String> ret = Sets.newTreeSet();
        ret.addAll(set);
        ret.removeAll(entries.keySet());
        return ret;
    }

    public Collection<T> valuesForKeys(final Set<String> keys)
    {
        final Map<String, T> map = Maps.newTreeMap();
        map.putAll(entries);
        map.keySet().retainAll(keys);
        return map.values();
    }

    public Map<String, T> asMap()
    {
        return ImmutableMap.copyOf(entries);
    }

    public Iterable<Map.Entry<String, T>> entries()
    {
        return asMap().entrySet();
    }

    @Override
    public DictionaryBuilder<T> thaw()
    {
        return new DictionaryBuilder<T>(this);
    }
}
