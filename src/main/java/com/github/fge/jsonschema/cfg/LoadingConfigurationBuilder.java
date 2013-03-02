package com.github.fge.jsonschema.cfg;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.exceptions.unchecked.DictionaryBuildError;
import com.github.fge.jsonschema.exceptions.unchecked.LoadingConfigurationError;
import com.github.fge.jsonschema.exceptions.unchecked.ValidationConfigurationError;
import com.github.fge.jsonschema.library.DictionaryBuilder;
import com.github.fge.jsonschema.load.DefaultDownloadersDictionary;
import com.github.fge.jsonschema.load.Dereferencing;
import com.github.fge.jsonschema.load.SchemaLoader;
import com.github.fge.jsonschema.load.URIDownloader;
import com.github.fge.jsonschema.load.URIManager;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.util.Thawed;
import com.google.common.collect.Maps;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static com.github.fge.jsonschema.messages.LoadingConfigurationMessages.*;

/**
 * Loading configuration (mutable instance)
 *
 * @see LoadingConfiguration
 */
public final class LoadingConfigurationBuilder
    implements Thawed<LoadingConfiguration>
{
    /**
     * The empty, default namespace
     */
    private static final URI EMPTY_NAMESPACE = URI.create("#");

    /**
     * Mutable dictionary of URI downloaders
     *
     * @see URIDownloader
     * @see URIManager
     */
    final DictionaryBuilder<URIDownloader> downloaders;

    /**
     * Loading default namespace
     *
     * @see SchemaLoader
     */
    URI namespace;

    /**
     * Dereferencing mode
     *
     * @see SchemaLoader
     */
    Dereferencing dereferencing;

    /**
     * List of schema redirects
     */
    final Map<URI, URI> schemaRedirects;

    /**
     * List of preloaded schemas
     *
     * <p>The default list of preloaded schemas consists of the draft v3 and
     * draft v4 core schemas</p>
     *
     * @see SchemaVersion
     */
    final Map<URI, JsonNode> preloadedSchemas;

    /**
     * Return a new, default mutable loading configuration
     *
     * @see LoadingConfiguration#newBuilder()
     */
    LoadingConfigurationBuilder()
    {
        downloaders = DefaultDownloadersDictionary.get().thaw();
        namespace = EMPTY_NAMESPACE;
        dereferencing = Dereferencing.CANONICAL;
        schemaRedirects = Maps.newHashMap();
        preloadedSchemas = Maps.newHashMap();
        for (final SchemaVersion version: SchemaVersion.values())
            preloadedSchemas.put(version.getLocation(), version.getSchema());
    }

    /**
     * Build a mutable loading configuration out of a frozen one
     *
     * @param cfg the frozen configuration
     * @see LoadingConfiguration#thaw()
     */
    LoadingConfigurationBuilder(final LoadingConfiguration cfg)
    {
        downloaders = cfg.downloaders.thaw();
        namespace = cfg.namespace;
        dereferencing = cfg.dereferencing;
        schemaRedirects = Maps.newHashMap(cfg.schemaRedirects);
        preloadedSchemas = Maps.newHashMap(cfg.preloadedSchemas);
    }

    /**
     * Add a new URI downloader
     *
     * @param scheme the scheme
     * @param downloader the downloader
     * @return this
     * @throws LoadingConfigurationError scheme is null or illegal
     * @throws DictionaryBuildError downloader is null
     */
    public LoadingConfigurationBuilder addScheme(final String scheme,
        final URIDownloader downloader)
    {
        downloaders.addEntry(checkScheme(scheme), downloader);
        return this;
    }

    /**
     * Remove a downloader for a given scheme
     *
     * @param scheme the scheme
     * @return this
     */
    public LoadingConfigurationBuilder removeScheme(final String scheme)
    {
        /*
         * No checks for null or anything there: adding entries will have been
         * filtered out anyway, so no harm.
         */
        downloaders.removeEntry(scheme);
        return this;
    }

    /**
     * Set the default namespace for that loading configuration
     *
     * @param input the namespace
     * @return this
     * @throws ValidationConfigurationError input is null or not an absolute
     * JSON Reference
     * @see RefSanityChecks#absoluteLocator(String)
     * @see JsonRef
     */
    public LoadingConfigurationBuilder setNamespace(final String input)
    {
        namespace = RefSanityChecks.absoluteLocator(input);
        return this;
    }

    /**
     * Set the dereferencing mode for this loading configuration
     *
     * <p>By default, it is {@link Dereferencing#CANONICAL}.</p>
     *
     * @param dereferencing the dereferencing mode
     * @return this
     * @throws LoadingConfigurationError dereferencing mode is null
     */
    public LoadingConfigurationBuilder dereferencing(
        final Dereferencing dereferencing)
    {
        if (dereferencing == null)
            throw new LoadingConfigurationError(new ProcessingMessage()
                .message(NULL_DEREFERENCING_MODE));
        this.dereferencing = dereferencing;
        return this;
    }

    /**
     * Add a schema redirection
     *
     * @param source URI of the source schema
     * @param destination URI to redirect to
     * @return this
     * @throws ValidationConfigurationError either the source or destination
     * URIs are null or not absolute JSON References
     * @throws LoadingConfigurationError source and destination are the same
     * @see JsonRef
     */
    public LoadingConfigurationBuilder addSchemaRedirect(final String source,
        final String destination)
    {
        final URI sourceURI = RefSanityChecks.absoluteLocator(source);
        final URI destinationURI = RefSanityChecks.absoluteLocator(destination);
        schemaRedirects.put(sourceURI, destinationURI);
        if (sourceURI.equals(destinationURI))
            throw new LoadingConfigurationError(new ProcessingMessage()
                .message(REDIRECT_TO_SELF).put("uri", sourceURI));
        return this;
    }

    /**
     * Preload a schema at a given URI
     *
     * <p>Use this if the schema you wish to preload does not have an absolute
     * {@code id} at the top level.</p>
     *
     * <p>Note that the syntax of the schema is not checked at this stage.</p>
     *
     * @param uri the URI to use
     * @param schema the schema
     * @return this
     * @throws LoadingConfigurationError the URI is null and/or not an absolute
     * JSON Reference, or the node is null
     * @see JsonRef
     */
    public LoadingConfigurationBuilder preloadSchema(final String uri,
        final JsonNode schema)
    {
        final ProcessingMessage message = new ProcessingMessage();

        if (schema == null)
            throw new LoadingConfigurationError(message.message(NULL_SCHEMA));
        final URI key = RefSanityChecks.absoluteLocator(uri);
        if (preloadedSchemas.containsKey(key))
            throw new LoadingConfigurationError(message.message(DUPLICATE_URI)
                .put("uri", key));
        preloadedSchemas.put(key, schema);
        return this;
    }

    /**
     * Preload a schema
     *
     * <p>Use this if the schema already has an absolute {@code id}.</p>
     *
     * @param schema the schema
     * @return this
     * @throws LoadingConfigurationError the schema is null, or it has no {@code
     * id}, or its {@code id} is not an absolute JSON Reference
     * @see JsonRef
     */
    public LoadingConfigurationBuilder preloadSchema(final JsonNode schema)
    {
        final JsonNode node = schema.path("id");
        if (!node.isTextual())
            throw new LoadingConfigurationError(new ProcessingMessage()
                .message(NO_ID_IN_SCHEMA));
        return preloadSchema(node.textValue(), schema);
    }

    /**
     * freeze this configuration
     *
     * @return a frozen copy of this builder
     */
    @Override
    public LoadingConfiguration freeze()
    {
        return new LoadingConfiguration(this);
    }

    private static String checkScheme(final String scheme)
    {
        final ProcessingMessage message = new ProcessingMessage();

        if (scheme == null)
            throw new LoadingConfigurationError(message.message(NULL_SCHEME));
        if (scheme.isEmpty())
            throw new LoadingConfigurationError(message.message(EMPTY_SCHEME));
        try {
            new URI(scheme, "x", "y");
        } catch (URISyntaxException ignored) {
            throw new LoadingConfigurationError(message.message(ILLEGAL_SCHEME)
                .put("scheme", scheme));
        }

        return scheme;
    }
}