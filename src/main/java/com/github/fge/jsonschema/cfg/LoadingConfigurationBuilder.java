package com.github.fge.jsonschema.cfg;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.unchecked.LoadingConfigurationError;
import com.github.fge.jsonschema.library.DictionaryBuilder;
import com.github.fge.jsonschema.library.SchemaVersion;
import com.github.fge.jsonschema.load.DefaultDownloadersDictionary;
import com.github.fge.jsonschema.load.Dereferencing;
import com.github.fge.jsonschema.load.URIDownloader;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.util.RefSanityChecks;
import com.github.fge.jsonschema.util.Thawed;
import com.google.common.collect.Maps;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static com.github.fge.jsonschema.messages.LoadingMessages.*;

public final class LoadingConfigurationBuilder
    implements Thawed<LoadingConfiguration>
{
    private static final URI EMPTY_NAMESPACE = URI.create("#");

    final DictionaryBuilder<URIDownloader> downloaders;
    URI namespace;
    Dereferencing dereferencing;
    final Map<URI, URI> schemaRedirects;
    final Map<URI, JsonNode> preloadedSchemas;

    LoadingConfigurationBuilder()
    {
        downloaders = DefaultDownloadersDictionary.get().thaw();
        namespace = EMPTY_NAMESPACE;
        dereferencing = Dereferencing.CANONICAL;
        schemaRedirects = Maps.newHashMap();
        preloadedSchemas = Maps.newHashMap();
        for (final SchemaVersion version: SchemaVersion.values())
            preloadedSchemas.put(version.getLocation().toURI(),
                version.getSchema());
    }

    LoadingConfigurationBuilder(final LoadingConfiguration cfg)
    {
        downloaders = cfg.downloaders.thaw();
        namespace = cfg.namespace;
        dereferencing = cfg.dereferencing;
        schemaRedirects = Maps.newHashMap(cfg.schemaRedirects);
        preloadedSchemas = Maps.newHashMap(cfg.preloadedSchemas);
    }

    public LoadingConfigurationBuilder addScheme(final String scheme,
        final URIDownloader downloader)
    {
        downloaders.addEntry(checkScheme(scheme), downloader);
        return this;
    }

    public LoadingConfigurationBuilder removeScheme(final String scheme)
    {
        /*
         * No checks for null or anything there: adding entries will have been
         * filtered out anyway, so no harm.
         */
        downloaders.removeEntry(scheme);
        return this;
    }

    public LoadingConfigurationBuilder setNamespace(final String input)
    {
        namespace = RefSanityChecks.absoluteLocator(input);
        return this;
    }

    public LoadingConfigurationBuilder dereferencing(
        final Dereferencing dereferencing)
    {
        this.dereferencing = dereferencing;
        return this;
    }

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

    public LoadingConfigurationBuilder preloadSchema(final String input,
        final JsonNode node)
    {
        final ProcessingMessage message = new ProcessingMessage();

        if (node == null)
            throw new LoadingConfigurationError(message.message(NULL_SCHEMA));
        final URI key = RefSanityChecks.absoluteLocator(input);
        if (preloadedSchemas.containsKey(key))
            throw new LoadingConfigurationError(message.message(DUPLICATE_URI)
                .put("uri", key));
        preloadedSchemas.put(key, node);
        return this;
    }

    public LoadingConfigurationBuilder preloadSchema(final JsonNode schema)
    {
        final JsonNode node = schema.path("id");
        if (!node.isTextual())
            throw new LoadingConfigurationError(new ProcessingMessage()
                .message(NO_ID_IN_SCHEMA));
        return preloadSchema(node.textValue(), schema);
    }

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