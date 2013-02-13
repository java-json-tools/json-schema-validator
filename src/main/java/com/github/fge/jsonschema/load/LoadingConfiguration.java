package com.github.fge.jsonschema.load;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.util.Frozen;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.net.URI;
import java.util.Map;

public final class LoadingConfiguration
    implements Frozen<LoadingConfigurationBuilder>
{
    final Dictionary<URIDownloader> downloaders;
    final URI namespace;
    final Dereferencing dereferencing;
    final Map<URI, URI> schemaRedirects;
    final Map<URI, JsonNode> preloadedSchemas;

    public static LoadingConfigurationBuilder newConfiguration()
    {
        return new LoadingConfigurationBuilder();
    }

    public static LoadingConfiguration byDefault()
    {
        return new LoadingConfigurationBuilder().freeze();
    }

    LoadingConfiguration(final LoadingConfigurationBuilder cfg)
    {
        downloaders = cfg.downloaders.freeze();
        namespace = cfg.namespace;
        dereferencing = cfg.dereferencing;
        schemaRedirects = Maps.newHashMap(cfg.schemaRedirects);
        preloadedSchemas = Maps.newHashMap(cfg.preloadedSchemas);
    }

    public Dictionary<URIDownloader> getDownloaders()
    {
        return downloaders;
    }

    public URI getNamespace()
    {
        return namespace;
    }

    public Dereferencing getDereferencing()
    {
        return dereferencing;
    }

    public Map<URI, URI> getSchemaRedirects()
    {
        return ImmutableMap.copyOf(schemaRedirects);
    }

    public Map<URI, JsonNode> getPreloadedSchemas()
    {
        return ImmutableMap.copyOf(preloadedSchemas);
    }

    @Override
    public LoadingConfigurationBuilder thaw()
    {
        return new LoadingConfigurationBuilder(this);
    }
}
