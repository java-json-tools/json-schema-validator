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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CollectionUtils
{
    public static <K, V> Map<K, V> toMap(final Iterator<Map.Entry<K, V>> iterator,
        final boolean allowDuplicates)
    {
        final Map<K, V> ret = new HashMap<K , V>();
        Map.Entry<K, V> entry;
        K key;
        V value;

        while (iterator.hasNext()) {
            entry = iterator.next();
            key = entry.getKey();
            value = entry.getValue();
            if (ret.containsKey(key) && !allowDuplicates)
                throw new IllegalArgumentException("Duplicate keys are not "
                    + "allowed");
            ret.put(key, value);
        }

        return ret;
    }

    public static <K, V> Map<K, V> toMap(final Iterator<Map.Entry<K, V>> iterator)
    {
        return toMap(iterator, true);
    }

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

    public static <T> Set<T> toSet(final Iterator<T> iterator)
    {
        return toSet(iterator, true);
    }

    public static <T> List<T> toList(final Iterator<T> iterator)
    {
        final List<T> ret = new ArrayList<T>();

        while (iterator.hasNext())
            ret.add(iterator.next());

        return ret;
    }
}
