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
import org.eel.kitchen.jsonschema.bundle.Keyword;
import org.eel.kitchen.jsonschema.bundle.KeywordBundle;
import org.eel.kitchen.jsonschema.bundle.KeywordBundles;
import org.eel.kitchen.jsonschema.format.FormatBundle;
import org.eel.kitchen.jsonschema.format.FormatSpecifier;
import org.eel.kitchen.jsonschema.ref.JsonPointer;
import org.eel.kitchen.jsonschema.ref.JsonRef;
import org.eel.kitchen.jsonschema.ref.SchemaContainer;
import org.eel.kitchen.jsonschema.ref.SchemaNode;
import org.eel.kitchen.jsonschema.ref.SchemaRegistry;
import org.eel.kitchen.jsonschema.uri.URIDownloader;
import org.eel.kitchen.jsonschema.uri.URIManager;
import org.eel.kitchen.jsonschema.validator.JsonValidatorCache;

import java.net.URI;
import java.util.EnumSet;

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
 *
 * @see JsonSchema
 */
public final class JsonSchemaFactory
{

    /**
     * Schema registry
     */
    private final SchemaRegistry registry;

    private final JsonValidatorCache cache;

    private final EnumSet<ValidationFeature> features;

    /**
     * Constructor, private by design
     *
     * @see JsonSchemaFactory.Builder
     * @param builder the builder
     */
    private JsonSchemaFactory(final Builder builder)
    {
        registry = new SchemaRegistry(builder.uriManager, builder.namespace);
        cache = new JsonValidatorCache(builder.keywordBundle, registry);
        features = EnumSet.copyOf(builder.features);
    }

    /**
     * Register a schema
     *
     * @param schema the raw schema
     * @return a schema container to instantiate a {@link JsonSchema}
     */
    public SchemaContainer registerSchema(final JsonNode schema)
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
        return new JsonSchema(cache, features, schemaNode);
    }

    /**
     * Builder class for a {@link JsonSchemaFactory}
     */
    public static final class Builder
    {
        /**
         * The keyword bundle
         */
        private KeywordBundle keywordBundle = KeywordBundles.defaultBundle();

        /**
         * The URI manager
         */
        private final URIManager uriManager = new URIManager();

        /**
         * The namespace
         */
        private URI namespace = URI.create("");

        private final EnumSet<ValidationFeature> features
            = EnumSet.noneOf(ValidationFeature.class);

        private FormatBundle formatBundle = FormatBundle.defaultBundle();

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

        /**
         * Add a schema keyword to the bundle
         *
         * @see Keyword
         *
         * @param keyword the keyword to add
         * @return the builder
         */
        public Builder registerKeyword(final Keyword keyword)
        {
            keywordBundle.registerKeyword(keyword);
            return this;
        }

        /**
         * Unregister a schema keyword
         *
         * @param name the name of the keyword to unregister
         * @return the builder
         */
        public Builder unregisterKeyword(final String name)
        {
            keywordBundle.unregisterKeyword(name);
            return this;
        }

        public Builder withKeywordBundle(final KeywordBundle keywordBundle)
        {
            this.keywordBundle = keywordBundle;
            return this;
        }

        public Builder addKeywords(final KeywordBundle keywordBundle)
        {
            this.keywordBundle.mergeWith(keywordBundle);
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
         * @throws IllegalArgumentException either of the arguments is an
         * invalid URI, or it is not an absolute JSON Reference
         */
        public Builder addRedirection(final String from, final String to)
        {
            uriManager.addRedirection(from, to);
            return this;
        }

        public Builder enableFeature(final ValidationFeature feature)
        {
            features.add(feature);
            return this;
        }

        public Builder registerFormat(final String fmt,
            final FormatSpecifier specifier)
        {
            formatBundle.registerFormat(fmt, specifier);
            return this;
        }

        public Builder unregisterFormat(final String fmt)
        {
            formatBundle.unregisterFormat(fmt);
            return this;
        }

        public Builder withFormatBundle(final FormatBundle formatBundle)
        {
            this.formatBundle = formatBundle;
            return this;
        }

        public Builder addFormats(final FormatBundle formatBundle)
        {
            this.formatBundle.mergeWith(formatBundle);
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
