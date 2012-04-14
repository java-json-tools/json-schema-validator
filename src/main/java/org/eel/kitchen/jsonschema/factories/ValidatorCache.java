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

package org.eel.kitchen.jsonschema.factories;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.util.LRUMap;
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.util.NodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.Map;

/**
 * Crude validator LRU cache -- but it works quite well
 *
 * <p>This cache relies on both the schema and the type of the validated
 * instance to cache a validator (it is perfectly possible that the validator
 * for a given schema is different from one type to another, since the keywords
 * in the schema may apply to different instance types).
 * </p>
 *
 * <p>This cache uses Jackson's {@link LRUMap} at its core.</p>
 */
public final class ValidatorCache
{
    private static final Logger logger
        = LoggerFactory.getLogger(ValidatorCache.class);
    /**
     * Initial size of an individual cache (one for each node type)
     */
    private static final int CACHE_INIT = 10;

    /**
     * Maximum size of an individual cache
     */
    private static final int CACHE_MAX = 50;

    /**
     * The {@link EnumMap} containing all caches
     *
     * <p>Keys are {@link NodeType} values, values are {@link LRUMap} instances
     * pairing a schema as a key and the matching validator as a value -- no
     * matter how complex.</p>
     */
    private final Map<NodeType, Map<JsonNode, Validator>> cache
        = new EnumMap<NodeType, Map<JsonNode, Validator>>(NodeType.class);

    public ValidatorCache()
    {
        for (final NodeType type: NodeType.values())
            cache.put(type, new LRUMap<JsonNode, Validator>(CACHE_INIT,
                CACHE_MAX));
    }

    /**
     * Get an entry from the cache
     *
     * @param type the type of the instance to validate
     * @param schema the schema to validate the instance against
     * @return the matching validator, or null if none is found
     */
    public Validator get(final NodeType type, final JsonNode schema)
    {
        return cache.get(type).get(schema);
    }

    /**
     * Add an entry to the cache
     *
     * @param type the type of the validated instance
     * @param schema the schema used to validate the instance
     * @param validator the validator
     */
    public void put(final NodeType type, final JsonNode schema,
        final Validator validator)
    {
        logger.debug("Registering new validator for type {}", type);
        cache.get(type).put(schema, validator);
    }
}
