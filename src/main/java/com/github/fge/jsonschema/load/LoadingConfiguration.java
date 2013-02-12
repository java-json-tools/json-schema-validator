package com.github.fge.jsonschema.load;

import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.util.Frozen;

import java.net.URI;

public final class LoadingConfiguration
    implements Frozen<LoadingConfigurationBuilder>
{
    final Dictionary<URIDownloader> downloaders;
    final URI namespace;

    public static LoadingConfigurationBuilder newConfiguration()
    {
        return new LoadingConfigurationBuilder();
    }

    LoadingConfiguration(final LoadingConfigurationBuilder cfg)
    {
        downloaders = cfg.downloaders.freeze();
        namespace = cfg.namespace;
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
