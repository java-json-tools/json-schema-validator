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

package org.eel.kitchen.jsonschema.ref;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.eel.kitchen.jsonschema.JsonSchemaException;
import org.eel.kitchen.jsonschema.schema.SchemaContainer;
import org.eel.kitchen.jsonschema.uri.URIManager;

import java.net.URI;
import java.util.concurrent.ExecutionException;

public final class SchemaRegistry
{
    private final LoadingCache<URI, SchemaContainer> cache;

    public SchemaRegistry()
    {
        this(new URIManager());
    }

    public SchemaRegistry(final URIManager manager)
    {
        cache = CacheBuilder.newBuilder().maximumSize(100)
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
        if (node == null)
            throw new IllegalArgumentException("schema is null");

        if (!node.path("id").isTextual())
            return SchemaContainer.anonymousSchema(node);

        final URI uri = JsonRef.fromNode(node, "id").getRootAsURI();
        final SchemaContainer container = new SchemaContainer(uri, node);

        cache.put(uri, container);
        return container;
    }

    SchemaContainer get(final URI uri)
        throws JsonSchemaException
    {
        try {
            return cache.get(uri);
        } catch (ExecutionException e) {
            throw new JsonSchemaException(e.getCause().getMessage());
        }
    }

    public void put(final URI uri, final JsonNode node)
        throws JsonSchemaException
    {
        if (!new JsonRef(uri).isAbsolute())
            throw new JsonSchemaException("URI " + uri + " is not a valid "
                + "JSON Schema locator");
        cache.put(uri, new SchemaContainer(uri, node));
    }
}
