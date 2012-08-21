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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.eel.kitchen.jsonschema.bundle.Keyword;
import org.eel.kitchen.jsonschema.bundle.KeywordBundle;
import org.eel.kitchen.jsonschema.bundle.KeywordBundles;
import org.eel.kitchen.jsonschema.keyword.KeywordFactory;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.ref.JsonPointer;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.jsonschema.uri.URIDownloader;
import org.eel.kitchen.jsonschema.uri.URIManager;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Factory to build JSON Schema validating instances
 *
 * <p>This class cannot be instantiated directly, you need to go through
 * its included {@link JsonSchemaFactory.Builder} to do that. This is so
 * that {@link JsonSchema} instances can be thread safe.</p>
 *
 * <p>Unless you have several sets of keywords, you will probably only ever
 * need one of these. It caches syntax validation results and keyword
 * validators to speed up validation (quite a bit).</p>
 *
 * <p>This class is thread safe.</p>
 */
public final class JsonSchemaFactory
{
    /**
     * Set of already validated schemas
     *
     * <p>FIXME: unbounded</p>
     */
    private final Set<JsonNode> validated = new HashSet<JsonNode>();

    /**
     * Cache for keyword validators
     */
    private final LoadingCache<JsonNode, Set<KeywordValidator>> cache;

    /**
     * Syntax validator
     */
    private final SyntaxValidator syntaxValidator;

    /**
     * Keyword factory
     */
    private final KeywordFactory keywordFactory;

    /**
     * Schema registry
     */
    private final SchemaRegistry registry;

    /**
     * Constructor, private by design
     *
     * @see JsonSchemaFactory.Builder
     * @param builder the builder
     */
    private JsonSchemaFactory(final Builder builder)
    {
        syntaxValidator = new SyntaxValidator(builder.bundle);
        keywordFactory = new KeywordFactory(builder.bundle);
        registry = new SchemaRegistry(builder.uriManager);

        final CacheLoader<JsonNode, Set<KeywordValidator>> loader
            = new CacheLoader<JsonNode, Set<KeywordValidator>>()
        {
            @Override
            public Set<KeywordValidator> load(final JsonNode key)
            {
                return keywordFactory.getValidators(key);
            }
        };

        cache = CacheBuilder.newBuilder().maximumSize(100L).build(loader);
    }

    /**
     * Register a schema
     *
     * @param schema the raw schema
     * @return a schema container to instantiate a {@link JsonSchema}
     * @throws JsonSchemaException illegal schema
     */
    public SchemaContainer registerSchema(final JsonNode schema)
        throws JsonSchemaException
    {
        return registry.register(schema);
    }

    /**
     * Get a schema container from a given URI
     *
     * <p>This is the other way to obtain a container (the other is
     * {@link #registerSchema(JsonNode)}).</p>
     *
     * @see SchemaRegistry#get(URI)
     *
     * @param uri the URI
     * @return a schema container
     * @throws JsonSchemaException cannot get schema from URI, or not a schema
     */
    public SchemaContainer getSchema(final URI uri)
        throws JsonSchemaException
    {
        return registry.get(uri);
    }

    /**
     * Create a schema from a container
     *
     * <p>This is one of the constructors you will use. The other is
     * {@link #createSchema(SchemaContainer, String)}.</p>
     *
     * @param container the schema container
     * @return a {@link JsonSchema} instance
     */
    public JsonSchema createSchema(final SchemaContainer container)
    {
        return createSchema(container, container.getSchema());
    }

    /**
     * Create a schema from a container, at a certain path
     *
     * <p>For instance, if you register this schema:</p>
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
     *     final JsonSchema schema = factory.create(container, "#/schema1");
     * </pre>
     *
     * <p>The path can be a {@link JsonPointer} as above,
     * but also an id reference.</p>
     *
     * @param container the schema container
     * @param path the pointer/id reference into the schema
     * @return a {@link JsonSchema} instance
     */
    public JsonSchema createSchema(final SchemaContainer container,
        final String path)
    {
        final JsonNode node = JsonNodeFactory.instance.objectNode()
            .put("$ref", path);

        return createSchema(container, node);
    }

    /**
     * Specialized constructor for validation internals
     *
     * <p>You should not use it in theory. I can dream ;)</p>
     *
     * @param container the schema container
     * @param schema the subschema
     * @return a {@link JsonSchema} instance
     */
    public JsonSchema createSchema(final SchemaContainer container,
        final JsonNode schema)
    {
        return new JsonSchema(this, new SchemaNode(container, schema));
    }

    /**
     * Piggyback method called by validators to check a schema syntax
     *
     * <p>You should not use it in theory. I can dream ;)</p>
     *
     * @param messages list of messages to fill
     * @param node the schema to validate
     */
    public void validateSyntax(final List<String> messages, final JsonNode node)
    {
        synchronized (validated) {
            if (validated.contains(node))
                return;
            syntaxValidator.validate(messages, node);
            if (messages.isEmpty())
                validated.add(node);
        }
    }

    /**
     * Piggyback method called by validators to obtain a keyword validator
     * set for a schema
     *
     * <p>You should not use it in theory. I can dream ;)</p>
     *
     * @param node the schema
     * @return a set of {@link KeywordValidator}
     */
    public Set<KeywordValidator> getValidators(final JsonNode node)
    {
        return cache.getUnchecked(node);
    }

    /**
     * Builder class for a {@link JsonSchemaFactory}
     */
    public static final class Builder
    {
        /**
         * The keyword bundle
         */
        private final KeywordBundle bundle;

        /**
         * The URI manager
         */
        private final URIManager uriManager;

        /**
         * No arg constructor
         *
         * <p>This calls {@link Builder#Builder(KeywordBundle)} with
         * {@link KeywordBundles#defaultBundle()} as an argument.</p>
         */
        public Builder()
        {
            this(KeywordBundles.defaultBundle());
        }

        /**
         * Main constructor
         *
         * @param bundle the keyword bundle to use
         */
        public Builder(final KeywordBundle bundle)
        {
            this.bundle = bundle;
            uriManager = new URIManager();
        }

        /**
         * Add a {@link URIDownloader} for a given URI scheme
         *
         * @param scheme the URI scheme
         * @param downloader the downloader
         * @return the builder
         * @throws NullPointerException scheme is null
         * @throws IllegalArgumentException illegal scheme
         */
        public Builder addURIDownloader(final String scheme,
            final URIDownloader downloader)
        {
            uriManager.registerDownloader(scheme, downloader);
            return this;
        }

        /**
         * Add a schema keyword to the bundle
         *
         * @see Keyword
         *
         * @param keyword the keyword to add
         * @return the builder
         */
        public Builder addKeyword(final Keyword keyword)
        {
            bundle.registerKeyword(keyword);
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
