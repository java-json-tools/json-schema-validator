/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

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
     * <p>Return a type-safe set, with optional checking for duplicate values
     * </p>
     *
     * @param iterator the iterator to build the set out of
     * @param allowDuplicates if false, forbid duplicates
     * @param <T> elements type
     * @return a type-safe {@link HashSet}
     * @throws IllegalArgumentException if allowDuplicates is false and the
     * iterator contains duplicate elements
     */
    public static <T> Set<T> toSet(final Iterator<T> iterator,
        final boolean allowDuplicates)
    {
        final Set<T> ret = new HashSet<T>();
        T element;

        while (iterator.hasNext()) {
            element = iterator.next();
            if (ret.contains(element) && !allowDuplicates)
                throw new IllegalArgumentException("Duplicate elements are not "
                    + "allowed");
            ret.add(element);
        }

        return ret;
    }

    /**
     * <p>Return a type-safe set out of an iterator, (sw)allowing duplicates.
     * This is equivalent to calling (and does call) <code>toSet(it,
     * true)</code>.
     * </p>
     *
     * @param iterator The iterator to build the set out of
     * @param <T> elements types
     * @return a type-safe {@link HashSet}
     */
    public static <T> Set<T> toSet(final Iterator<T> iterator)
    {
        return toSet(iterator, true);
    }

    public static <T> SortedSet<T> toSortedSet(final Iterator<T> iterator)
    {
        final SortedSet<T> ret = new TreeSet<T>();

        while (iterator.hasNext())
            ret.add(iterator.next());

        return ret;
    }
}
