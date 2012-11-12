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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.eel.kitchen.jsonschema.metaschema.KeywordRegistries;
import org.eel.kitchen.jsonschema.metaschema.KeywordRegistry;
import org.eel.kitchen.jsonschema.metaschema.SchemaURIs;
import org.eel.kitchen.jsonschema.ref.JsonFragment;
import org.eel.kitchen.jsonschema.ref.JsonPointer;
import org.eel.kitchen.jsonschema.ref.JsonRef;
import org.eel.kitchen.jsonschema.schema.AddressingMode;
import org.eel.kitchen.jsonschema.schema.SchemaContainer;
import org.eel.kitchen.jsonschema.schema.SchemaNode;
import org.eel.kitchen.jsonschema.schema.SchemaRegistry;
import org.eel.kitchen.jsonschema.uri.URIDownloader;
import org.eel.kitchen.jsonschema.uri.URIManager;
import org.eel.kitchen.jsonschema.validator.JsonValidatorCache;

import java.net.URI;
import java.util.Map;

/**
 * Factory to build JSON Schema validating instances
 *
 * <p>You can create a factory with all default settings using {@link
 * JsonSchemaFactory#defaultFactory()}. This is what you will do in the vast
 * majority of cases.</p>
 *
 * <p>If you want to customize it, you need to go through {@link
 * JsonSchemaFactory.Builder}.</p>
 *
 * <p>This class is thread safe and immutable.</p>
 *
 * @see JsonSchema
 */
public final class JsonSchemaFactory
{
    /**
     * Schema registry
     */
    private final SchemaRegistry registry;

    /**
     * Default schema URI
     */
    private final JsonRef defaultSchemaURI;

    /**
     * Map of schema URIs and validator caches
     */
    private final Map<JsonRef, JsonValidatorCache> validatorCaches;

    /**
     * Build a factory with all default settings
     *
     * @return a schema factory instance
     */
    public static JsonSchemaFactory defaultFactory()
    {
        return new Builder().build();
    }

    /**
     * Constructor, private by design
     *
     * @see JsonSchemaFactory.Builder
     * @param builder the builder
     */
    private JsonSchemaFactory(final Builder builder)
    {
        registry = new SchemaRegistry(builder.uriManager, builder.namespace,
            builder.addressingMode);
        defaultSchemaURI = builder.defaultSchemaURI;

        final ImmutableMap.Builder<JsonRef, JsonValidatorCache> cacheBuilder
            = ImmutableMap.builder();
        final Map<JsonRef, KeywordRegistry> map = builder.keywordRegistries;

        JsonRef ref;
        JsonValidatorCache validatorCache;

        for (final Map.Entry<JsonRef, KeywordRegistry> entry: map.entrySet()) {
            ref = entry.getKey();
            validatorCache = new JsonValidatorCache(entry.getValue(), registry);
            cacheBuilder.put(ref, validatorCache);
        }

        validatorCaches = cacheBuilder.build();
    }

    /**
     * Create a schema instance from a JSON Schema, at a certain path
     *
     * <p>For instance, if you submit this schema:</p>
     *
     * <pre>
     *     {
     *         "schema1": { ... },
     *         "schema2": { ... }
     *     }
     * </pre>
     *
     * <p>then you can create a validator for {@code schema1} using:</p>
     *
     * <pre>
     *     final JsonSchema schema = factory.create(schema, "#/schema1");
     * </pre>
     *
     * <p>The path can be a {@link JsonPointer} as above, but also an id
     * reference.</p>
     *
     * @see JsonFragment
     *
     * @param schema the schema
     * @param path the pointer/id reference into the schema
     * @return a {@link JsonSchema} instance
     */
    public JsonSchema fromSchema(final JsonNode schema, final String path)
    {
        final SchemaContainer container = registry.register(schema);
        final JsonNode subSchema = JsonFragment.fromFragment(path)
            .resolve(container.getSchema());
        return createSchema(container, subSchema);
    }

    /**
     * Create a schema instance from a JSON Schema
     *
     * <p>This calls {@link #fromSchema(JsonNode, String)} with {@code ""} as
     * a path.</p>
     *
     * @param schema the schema
     * @return a {@link JsonSchema} instance
     */
    public JsonSchema fromSchema(final JsonNode schema)
    {
        return fromSchema(schema, "");
    }

    /**
     * Create a schema instance from a JSON Schema located at a given URI, and
     * at a given path
     *
     * <p>This allows you, for instance, to load a schema using HTTP. Or, in
     * fact, any other URI scheme that is supported.</p>
     *
     * @see URIManager
     * @see SchemaRegistry
     *
     * @param uri the URI
     * @param path the JSON Pointer/id reference into the downloaded schema
     * @return a {@link JsonSchema} instance
     * @throws JsonSchemaException unable to get content from that URI
     */
    public JsonSchema fromURI(final URI uri, final String path)
        throws JsonSchemaException
    {
        final SchemaContainer container = registry.get(uri);
        final JsonNode subSchema = JsonFragment.fromFragment(path)
            .resolve(container.getSchema());
        return createSchema(container, subSchema);
    }

    /**
     * Create a schema instance from a JSON Schema located at a given URI
     *
     * @see #fromSchema(JsonNode, String)
     *
     * @param uri the URI
     * @return a {@link JsonSchema} instance
     * @throws JsonSchemaException unable to get content from that URI
     */
    public JsonSchema fromURI(final URI uri)
        throws JsonSchemaException
    {
        return fromURI(uri, "");
    }

    /**
     * Create a schema instance from a JSON Schema located at a given URI
     *
     * @see URI#create(String)
     * @see #fromURI(URI, String)
     *
     * @param str the URI as a string
     * @return a {@link JsonSchema} instance
     * @throws JsonSchemaException unable to get content from that URI
     * @throws IllegalArgumentException URI is invalid
     */
    public JsonSchema fromURI(final String str)
        throws JsonSchemaException
    {
        return fromURI(URI.create(str), "");
    }

    /**
     * Create a schema instance from a JSON Schema located at a given URI and
     * at a given path
     *
     * @see URI#create(String)
     * @see #fromURI(URI, String)
     *
     * @param str the URI as a string
     * @param  path the JSON Pointer/id reference into the downloaded schema
     * @return a {@link JsonSchema} instance
     * @throws JsonSchemaException unable to get content from that URI
     * @throws IllegalArgumentException URI is invalid
     */
    public JsonSchema fromURI(final String str, final String path)
        throws JsonSchemaException
    {
        return fromURI(URI.create(str), path);
    }

    /**
     * Create a {@link JsonSchema} instance
     *
     * @param container the schema container
     * @param schema the subschema
     * @return a {@link JsonSchema} instance
     */
    private JsonSchema createSchema(final SchemaContainer container,
        final JsonNode schema)
    {
        final SchemaNode schemaNode = new SchemaNode(container, schema);
        final JsonValidatorCache cache
            = getValidatorCache(container.getSchema());
        return new JsonSchema(cache, schemaNode);
    }

    private JsonValidatorCache getValidatorCache(final JsonNode schema)
    {
        final JsonNode node = schema.path("$schema");

        if (!node.isTextual())
            return validatorCaches.get(defaultSchemaURI);

        JsonRef ref;

        try {
            ref = JsonRef.fromString(node.textValue());
            if (!validatorCaches.containsKey(ref))
                ref = defaultSchemaURI;
        } catch (JsonSchemaException ignored) {
            ref = defaultSchemaURI;
        }

        return validatorCaches.get(ref);
    }

    /**
     * Builder class for a {@link JsonSchemaFactory}
     */
    public static final class Builder
    {
        /**
         * Addressing mode
         */
        private AddressingMode addressingMode;

        /**
         * Default schema URI
         */
        private JsonRef defaultSchemaURI;

        /**
         * Default keyword registry
         */
        private KeywordRegistry defaultKeywordRegistry;

        /**
         * Keyword registries
         */
        private final Map<JsonRef, KeywordRegistry> keywordRegistries;

        /**
         * The URI manager
         */
        private final URIManager uriManager;

        /**
         * The namespace
         */
        private URI namespace;

        /*
         * Deprecated instance fields
         */

        /**
         * Constructor
         */
        public Builder()
        {
            /*
             * Initialize all default values
             */
            // URI related
            namespace = URI.create("");
            uriManager = new URIManager();
            addressingMode = AddressingMode.CANONICAL;

            // Metaschema related
            defaultSchemaURI = SchemaURIs.defaultURI();
            defaultKeywordRegistry = KeywordRegistries.defaultRegistry();
            keywordRegistries = Maps.newHashMap();
            keywordRegistries.put(defaultSchemaURI, defaultKeywordRegistry);
            keywordRegistries.put(SchemaURIs.draftV4(),
                KeywordRegistries.draftV4());
        }

        /**
         * Register a {@link URIDownloader} for a given scheme
         *
         * @param scheme the URI scheme
         * @param downloader the downloader
         * @return the builder
         * @throws NullPointerException scheme is null
         * @throws IllegalArgumentException illegal scheme
         */
        public Builder registerScheme(final String scheme,
            final URIDownloader downloader)
        {
            uriManager.registerScheme(scheme, downloader);
            return this;
        }

        /**
         * Unregister a scheme
         *
         * @param scheme the scheme to desupport
         * @return the builder
         */
        public Builder unregisterScheme(final String scheme)
        {
            uriManager.unregisterScheme(scheme);
            return this;
        }

        public Builder addressingMode(final AddressingMode addressingMode)
        {
            this.addressingMode = addressingMode;
            return this;
        }

        /**
         * Set the schema registry's namespace
         *
         * @see SchemaRegistry
         *
         * @param namespace the namespace, as a string
         * @return the builder
         * @throws IllegalArgumentException invalid URI (see {@link
         * URI#create(String)})
         */
        public Builder setNamespace(final String namespace)
        {
            this.namespace = URI.create(namespace);
            return this;
        }

        /**
         * Add an URI redirection
         *
         * <p>This allows you to add an alias for a schema location so that it
         * point to another of your choice. It may be useful if you have to
         * resolve absolute JSON References normally unreachable, but you have
         * a copy of this schema locally.</p>
         *
         * <p>Note that both URIs must be absolute.</p>
         *
         * @see JsonRef
         *
         * @param from the source URI, as a string
         * @param to the target URI, as a string
         * @return the builder
         * @throws IllegalArgumentException either {@code from} or {@code to}
         * is an  invalid URI, or it is not an absolute JSON Reference
         */
        public Builder addRedirection(final String from, final String to)
        {
            uriManager.addRedirection(from, to);
            return this;
        }

        public Builder addKeywordRegistry(final JsonRef schemaURI,
            final KeywordRegistry keywordRegistry, final boolean byDefault)
        {
            Preconditions.checkNotNull(schemaURI, "schema URI cannot be null");
            Preconditions.checkNotNull(keywordRegistry,
                "keyword registry cannot be null");

            keywordRegistries.put(schemaURI, keywordRegistry);
            if (byDefault) {
                defaultSchemaURI = schemaURI;
                defaultKeywordRegistry = keywordRegistry;
            }

            return this;
        }

        /**
         * Build the factory
         *
         * @return the factory
         */
        public JsonSchemaFactory build()
        {
            return new JsonSchemaFactory(this);
        }
    }
}
