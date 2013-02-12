package com.github.fge.jsonschema.load;

import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.util.Frozen;

public final class LoadingConfiguration
    implements Frozen<LoadingConfigurationBuilder>
{
    final Dictionary<URIDownloader> downloaders;

    public static LoadingConfigurationBuilder newConfiguration()
    {
        return new LoadingConfigurationBuilder();
    }

    LoadingConfiguration(final LoadingConfigurationBuilder cfg)
    {
        downloaders = cfg.downloaders.freeze();
    }

    @Override
    public LoadingConfigurationBuilder thaw()
    {
        return new LoadingConfigurationBuilder(this);
    }
}
