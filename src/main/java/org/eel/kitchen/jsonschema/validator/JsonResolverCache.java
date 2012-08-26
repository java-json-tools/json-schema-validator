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

package org.eel.kitchen.jsonschema.validator;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.main.SchemaRegistry;

import java.util.concurrent.ExecutionException;

public final class JsonResolverCache
{
    private final LoadingCache<SchemaNode, SchemaNode> cache;

    public JsonResolverCache(final SchemaRegistry registry)
    {
        final CacheLoader<SchemaNode, SchemaNode> loader
            = buildCacheLoader(new JsonResolver(registry));

        cache = CacheBuilder.newBuilder().maximumSize(100L)
            .build(loader);
    }

    private static CacheLoader<SchemaNode, SchemaNode> buildCacheLoader(
        final JsonResolver resolver)
    {
        return new CacheLoader<SchemaNode, SchemaNode>()
        {
            @Override
            public SchemaNode load(final SchemaNode key)
                throws JsonSchemaException
            {
                return resolver.resolve(key);
            }
        };
    }

    SchemaNode get(final SchemaNode key)
        throws JsonSchemaException
    {
        try {
            return cache.get(key);
        } catch (ExecutionException e) {
            throw new JsonSchemaException(e.getMessage());
        }
    }
}
