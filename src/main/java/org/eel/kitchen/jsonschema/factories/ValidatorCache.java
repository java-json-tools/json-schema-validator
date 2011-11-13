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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.factories;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.util.LRUMap;
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.util.NodeType;

import java.util.EnumMap;
import java.util.Map;

public final class ValidatorCache
{
    private static final int CACHE_INIT = 10;
    private static final int CACHE_MAX = 50;

    private final Map<NodeType, Map<JsonNode, Validator>> cache
        = new EnumMap<NodeType, Map<JsonNode, Validator>>(NodeType.class);

    public ValidatorCache()
    {
        for (final NodeType type: NodeType.values())
            cache.put(type,
                new LRUMap<JsonNode, Validator>(CACHE_INIT, CACHE_MAX));
    }

    public Validator get(final NodeType type, final JsonNode schema)
    {
        return cache.get(type).get(schema);
    }

    public void put(final NodeType type, final JsonNode schema,
        final Validator validator)
    {
        cache.get(type).put(schema, validator);
    }

    public void clear()
    {
        for (final NodeType type: NodeType.values())
            cache.get(type).clear();
    }
}
