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

final class MapBuilder<T>
{
    private final ImmutableMap.Builder<String, T> builder;

    private MapBuilder()
    {
        builder = new ImmutableMap.Builder<String, T>();
    }

    public static <T> MapBuilder<T> create()
    {
        return new MapBuilder<T>();
    }

    public void put(final String keyword, final T value)
    {
        builder.put(keyword, value);
    }

    public void putAll(final Map<String, T> map)
    {
        builder.putAll(map);
    }

    public Map<String, T> build()
    {
        return builder.build();
    }
}
