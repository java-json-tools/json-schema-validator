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

package org.eel.kitchen.jsonschema.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.ref.JsonRef;
import org.eel.kitchen.jsonschema.report.Domain;
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.uri.URIManager;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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
public final class SchemaRegistry
{
    /**
     * The default namespace
     */
    private final JsonRef namespace;

    /**
     * Schema cache
     */
    private final LoadingCache<URI, SchemaContainer> cache;

    /**
     * Addressing mode
     */

    private final AddressingMode addressingMode;

    /**
     * Constructor
     *
     * @param manager the URI manager to use
     * @param namespace this registry's namespace
     * @param addressingMode the addressing mode for this registry
     */
    public SchemaRegistry(final URIManager manager, final URI namespace,
        final AddressingMode addressingMode)
    {
        this.addressingMode = addressingMode;
        this.namespace = JsonRef.fromURI(namespace);
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

    public SchemaRegistry(final URIManager manager, final URI namespace)
    {
        this(manager, namespace, AddressingMode.CANONICAL);
    }

    /**
     * Register a schema
     *
     * @param schema the schema to register
     * @return a schema container
     */
    public SchemaContainer register(final JsonNode schema)
    {
        Preconditions.checkNotNull(schema, "cannot register null schema");

        final SchemaContainer container = new SchemaContainer(schema);

        final JsonRef ref = container.getLocator();

        if (ref.isAbsolute())
            cache.put(ref.getLocator(), container);

        return container;
    }

    /**
     * Get a schema container from the given URI
     *
     * <p>Note that if the URI is relative, it will be resolved against this
     * registry's namespace, if any.</p>
     *
     * @param uri the URI
     * @return a schema container
     * @throws JsonSchemaException impossible to get content at this URI
     */
    public SchemaContainer get(final URI uri)
        throws JsonSchemaException
    {
        final JsonRef ref = namespace.resolve(JsonRef.fromURI(uri));

        final Message.Builder msg = Domain.REF_RESOLVING.newMessage()
            .setFatal(true).setKeyword("N/A").addInfo("uri", ref);

        if (!ref.isAbsolute())
            throw new JsonSchemaException(msg.setMessage("URI is not absolute")
                .build());

        final URI realURI = ref.toURI();

        try {
            return cache.get(realURI);
        } catch (ExecutionException e) {
            final Throwable cause = e.getCause();
            msg.addInfo("exception-class", cause.getClass().getName())
                .addInfo("exception-message", cause.getMessage());
            throw new JsonSchemaException(msg.build());
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
        final Map<URI, JsonNode> map = bundle.getSchemas();

        SchemaContainer container;
        URI uri;
        JsonNode schema;
        for (final Map.Entry<URI, JsonNode> entry: map.entrySet()) {
            uri = entry.getKey();
            schema = entry.getValue();
            container = new SchemaContainer(uri, schema);
            cache.put(uri, container);
        }
    }
}
