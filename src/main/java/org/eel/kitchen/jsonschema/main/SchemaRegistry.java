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

package org.eel.kitchen.jsonschema.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.eel.kitchen.jsonschema.ref.JsonRef;
import org.eel.kitchen.jsonschema.uri.URIManager;

import java.net.URI;
import java.util.concurrent.ExecutionException;

/**
 * As its name says, a schema registry
 */
public final class SchemaRegistry
{
    private final URI namespace;
    private final LoadingCache<URI, SchemaContainer> cache;

    SchemaRegistry(final URIManager manager, final URI namespace)
    {
        this.namespace = namespace.normalize();
        cache = CacheBuilder.newBuilder().maximumSize(100L)
            .build(new CacheLoader<URI, SchemaContainer>()
            {
                @Override
                public SchemaContainer load(final URI key)
                    throws JsonSchemaException
                {
                    return new SchemaContainer(key, manager.getContent(key));
                }
            });
    }

    public SchemaContainer register(final JsonNode node)
        throws JsonSchemaException
    {
        Preconditions.checkNotNull(node, "cannot register null schema");

        final SchemaContainer container = new SchemaContainer(node);

        final JsonRef ref = container.getLocator();

        if (ref.isAbsolute())
            cache.put(ref.getRootAsURI(), container);

        return container;
    }

    public SchemaContainer get(final URI uri)
        throws JsonSchemaException
    {
        try {
            return cache.get(namespace.resolve(uri).normalize());
        } catch (ExecutionException e) {
            throw new JsonSchemaException(e.getCause().getMessage());
        }
    }
}
