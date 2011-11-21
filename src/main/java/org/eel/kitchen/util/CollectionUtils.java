/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
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

package org.eel.kitchen.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * <p>Utilities to provide simple generics equivalents to some of Apache's
 * commons-collection package (which still doesn't use generics as of 2011!)
 * </p>
 */

public final class CollectionUtils
{
    /**
     * <p>Generics equivalent of commons-collections' IteratorUtils.toMap()</p>
     *
     * @param iterator The entry iterator to build the map out of
     * @param <K> keys type
     * @param <V> values type
     * @return a type-safe {@link HashMap}
     */
    public static <K, V> Map<K, V> toMap(final Iterator<Map.Entry<K, V>> iterator)
    {
        final Map<K, V> ret = new HashMap<K , V>();
        Map.Entry<K, V> entry;

        while (iterator.hasNext()) {
            entry = iterator.next();
            ret.put(entry.getKey(), entry.getValue());
        }

        return ret;
    }

    /**
     * Like #toMap, but returns a {@link SortedMap} instead of a plain Map.
     * Note that it returns a {@link TreeMap}, which is <b>not</b>
     * thread-safe (we don't need it in our context anyway).
     *
     * @param iterator The entry iterator to build the map out of
     * @param <K> keys type
     * @param <V> values type
     * @return a type-safe {@link TreeMap}
     */
    public static <K, V> SortedMap<K, V> toSortedMap(
        final Iterator<Map.Entry<K, V>> iterator)
    {
        final SortedMap<K, V> ret = new TreeMap<K, V>();
        Map.Entry<K, V> entry;

        while (iterator.hasNext()) {
            entry = iterator.next();
            ret.put(entry.getKey(), entry.getValue());
        }

        return ret;
    }

    /**
     * <p>Return a "type-safe" set
     * </p>
     *
     * @param iterator the iterator to build the set out of
     * @param <T> elements type
     * @return a type-safe {@link HashSet}
     */
    public static <T> Set<T> toSet(final Iterator<T> iterator)
    {
        final Set<T> ret = new HashSet<T>();

        while (iterator.hasNext())
            ret.add(iterator.next());

        return ret;
    }
}
