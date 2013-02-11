/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.jsonschema.load;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.net.URI;
import java.util.concurrent.ExecutionException;

import static com.github.fge.jsonschema.messages.RefProcessingMessages.*;

/**
 * A JSON Schema registry
 *
 * <p>All schema registering and downloading is done through this class.</p>
 *
 * <p>Note that if the id of a schema is not absolute (that is, the URI itself
 * is absolute and it has no fragment part, or an empty fragment), then the
 * whole schema will be considered anonymous.</p>
 *
 * <p>This class is thread safe.</p>
 */
public final class SchemaLoader
{
    private static final URI EMPTY_NAMESPACE = URI.create("#");
    /**
     * The default namespace
     */
    private final JsonRef namespace;

    /**
     * Schema cache
     */
    private final LoadingCache<URI, JsonNode> cache;

    /**
     * Our dereferencing mode
     */
    private final Dereferencing dereferencing;

    /**
     * Constructor
     *
     * @param manager the URI manager to use
     * @param namespace this registry's namespace
     * @param dereferencing our {@link Dereferencing} mode
     */
    public SchemaLoader(final URIManager manager, final URI namespace,
        final Dereferencing dereferencing)
    {
        this.dereferencing = dereferencing;
        this.namespace = JsonRef.fromURI(namespace);
        cache = CacheBuilder.newBuilder().maximumSize(100L)
            .build(new CacheLoader<URI, JsonNode>()
            {
                @Override
                public JsonNode load(final URI key)
                    throws ProcessingException
                {
                    return manager.getContent(key);
                }
            });
    }

    public SchemaLoader(final URIManager manager,
        final Dereferencing dereferencing)
    {
        this(manager, EMPTY_NAMESPACE, dereferencing);
    }

    public SchemaTree load(final JsonNode schema)
    {
        Preconditions.checkNotNull(schema, "cannot register null schema");
        return dereferencing.newTree(schema);
    }

    /**
     * Get a schema tree from the given URI
     *
     * <p>Note that if the URI is relative, it will be resolved against this
     * registry's namespace, if any.</p>
     *
     * @param uri the URI
     * @return a schema tree
     */
    public SchemaTree get(final URI uri)
        throws ProcessingException
    {
        final JsonRef ref = namespace.resolve(JsonRef.fromURI(uri));

        final ProcessingMessage msg = new ProcessingMessage()
            .put("uri", ref);

        if (!ref.isAbsolute())
            throw new ProcessingException(msg.message(URI_NOT_ABSOLUTE));

        final URI realURI = ref.toURI();

        try {
            final JsonNode node = cache.get(realURI);
            return dereferencing.newTree(ref, node);
        } catch (ExecutionException e) {
            throw (ProcessingException) e.getCause();
        }
    }

    /**
     * Load a schema bundle into this registry
     *
     * <p>As a schema bundle is guaranteed to have well-formed locators, schemas
     * from a bundle can be directly injected into the cache.</p>
     */
    public void addBundle(final SchemaBundle bundle)
    {
        cache.putAll(bundle.getSchemas());
    }

    @Override
    public String toString()
    {
        return cache.stats().toString();
    }
}
