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
import org.eel.kitchen.jsonschema.format.FormatAttribute;
import org.eel.kitchen.jsonschema.format.FormatBundle;
import org.eel.kitchen.jsonschema.ref.JsonFragment;
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
     * Validator cache
     */
    private final JsonValidatorCache cache;

    /**
     * List of supported features
     */
    private final EnumSet<ValidationFeature> features;

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
        registry = new SchemaRegistry(builder.uriManager, builder.namespace);
        cache = new JsonValidatorCache(builder.keywordBundle, registry);
        features = EnumSet.copyOf(builder.features);
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

        /**
         * The feature set
         */
        private final EnumSet<ValidationFeature> features
            = EnumSet.noneOf(ValidationFeature.class);

        /**
         * The format bundle
         */
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

        /**
         * Replace the keyword bundle with an entirely new bundle
         *
         * <p>Use with caution!</p>
         *
         * @param keywordBundle the bundle
         * @return the builder
         */
        public Builder withKeywordBundle(final KeywordBundle keywordBundle)
        {
            this.keywordBundle = keywordBundle;
            return this;
        }

        /**
         * Merge the existing keyword bundle with another, custom bundle
         *
         * @see KeywordBundle#mergeWith(KeywordBundle)
         *
         * @param keywordBundle the bundle
         * @return the builder
         */
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
         * @throws IllegalArgumentException either {@code from} or {@code to}
         * is an  invalid URI, or it is not an absolute JSON Reference
         */
        public Builder addRedirection(final String from, final String to)
        {
            uriManager.addRedirection(from, to);
            return this;
        }

        /**
         * Enable a validation feature
         *
         * @param feature the feature to set
         * @return the builder
         */
        public Builder enableFeature(final ValidationFeature feature)
        {
            features.add(feature);
            return this;
        }

        /**
         * Register a format attribute
         *
         * @see FormatBundle#registerFormat(String, FormatAttribute)
         *
         * @param fmt the name for this attribute
         * @param attribute the format attribute instance
         * @return the builder
         */
        public Builder registerFormat(final String fmt,
            final FormatAttribute attribute)
        {
            formatBundle.registerFormat(fmt, attribute);
            return this;
        }

        /**
         * Unregister a format attribute
         *
         * <p>This is a no op if such a attribute was not registered.</p>
         *
         * @param fmt the name for this attribute
         * @return the builder
         */
        public Builder unregisterFormat(final String fmt)
        {
            formatBundle.unregisterFormat(fmt);
            return this;
        }

        /**
         * Replace the format bundle with a custom bundle
         *
         * <p>Use with caution! In particular, you <b>should not</b> mess with
         * {@code uri} and {@code regex}.</p>
         *
         * @see FormatBundle#defaultBundle()
         *
         * @param formatBundle the bundle
         * @return the builder
         */
        public Builder withFormatBundle(final FormatBundle formatBundle)
        {
            this.formatBundle = formatBundle;
            return this;
        }

        /**
         * Merge the existing bundle with another, custom bundle
         *
         * @see FormatBundle#mergeWith(FormatBundle)
         *
         * @param formatBundle the bundle
         * @return the builder
         */
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

    /*
     * Deprecated stuff
     */

    /**
     * Register a schema
     *
     * @param schema the raw schema
     * @return a schema container to instantiate a {@link JsonSchema}
     *
     * @deprecated use {@link #fromSchema(JsonNode)} instead; scheduled for
     * removal in 1.3+
     */
    @Deprecated
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
     *
     * @deprecated use {@link #fromURI(URI)} instead; scheduled for removal
     * in 1.3+
     */
    @Deprecated
    public SchemaContainer getSchema(final URI uri)
        throws JsonSchemaException
    {
        return registry.get(uri);
    }

    /**
     * Get a schema container from a given URI
     *
     * <p>This calls {@link #getSchema(URI)} and uses {@link URI#create(String)}
     * to build the URI</p>
     *
     * @see URI#create(String)
     *
     * @param str the URI as a string
     * @return a schema container
     * @throws JsonSchemaException cannot get schema from URI, or not a schema
     * @throws IllegalArgumentException string is not a valid URI
     *
     * @deprecated use {@link #fromURI(String)} instead; scheduled for removal
     * in 1.3+
     */
    @Deprecated
    public SchemaContainer getSchema(final String str)
        throws JsonSchemaException
    {
        return registry.get(URI.create(str));
    }

    /**
     * Create a schema from a container
     *
     * <p>This is one of the constructors you will use. The other is
     * {@link #createSchema(SchemaContainer, String)}.</p>
     *
     * @param container the schema container
     * @return a {@link JsonSchema} instance
     *
     * @deprecated use one of the {@code .from*()} methods instead; scheduled
     * for removal in 1.3+
     */
    @Deprecated
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
     *
     * @deprecated use {@link #fromSchema(JsonNode, String)} instead; scheduled
     * for removal in 1.3+
     */
    @Deprecated
    public JsonSchema createSchema(final SchemaContainer container,
        final String path)
    {
        final JsonNode node = JsonNodeFactory.instance.objectNode()
            .put("$ref", path);

        return createSchema(container, node);
    }
}
