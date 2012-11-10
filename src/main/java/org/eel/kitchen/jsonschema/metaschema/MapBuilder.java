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

package org.eel.kitchen.jsonschema.metaschema;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Utility class to build maps
 *
 * <p>All maps dealt with by this class have {@link String} instances as keys.
 * </p>
 *
 * @param <T> value type
 */

final class MapBuilder<T>
{
    private final ImmutableMap.Builder<String, T> builder;

    private MapBuilder()
    {
        builder = ImmutableMap.builder();
    }

    /**
     * Create a new instance
     *
     * @param <T> value type
     * @return a new builder
     */
    public static <T> MapBuilder<T> create()
    {
        return new MapBuilder<T>();
    }

    /**
     * Add one entry to this builder
     *
     * @param key the key
     * @param value the value
     */
    public void put(final String key, final T value)
    {
        builder.put(key, value);
    }

    /**
     * Add all entries from an existing map
     *
     * @param map the map to add
     */
    public void putAll(final Map<String, T> map)
    {
        builder.putAll(map);
    }

    /**
     * Return an immutable map from this builder
     *
     * @return a map
     */
    public Map<String, T> build()
    {
        return builder.build();
    }
}
