package com.github.fge.jsonschema.load;

import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.util.Frozen;
import com.google.common.collect.ImmutableMap;

import java.net.URI;
import java.util.Map;

public final class LoadingConfiguration
    implements Frozen<LoadingConfigurationBuilder>
{
    final Dictionary<URIDownloader> downloaders;
    final URI namespace;
    final Map<URI, URI> schemaRedirects;

    public static LoadingConfigurationBuilder newConfiguration()
    {
        return new LoadingConfigurationBuilder();
    }

    LoadingConfiguration(final LoadingConfigurationBuilder cfg)
    {
        downloaders = cfg.downloaders.freeze();
        namespace = cfg.namespace;
        schemaRedirects = ImmutableMap.copyOf(cfg.schemaRedirects);
    }

    public Dictionary<URIDownloader> downloaders()
    {
        return downloaders;
    }

    @Override
    public LoadingConfigurationBuilder thaw()
    {
        return new LoadingConfigurationBuilder(this);
    }
}
