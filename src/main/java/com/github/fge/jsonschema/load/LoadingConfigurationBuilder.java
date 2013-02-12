package com.github.fge.jsonschema.load;

import com.github.fge.jsonschema.exceptions.unchecked.LoadingConfigurationError;
import com.github.fge.jsonschema.library.DictionaryBuilder;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.util.Thawed;

import java.net.URI;
import java.net.URISyntaxException;

import static com.github.fge.jsonschema.messages.LoadingMessages.*;

public final class LoadingConfigurationBuilder
    implements Thawed<LoadingConfiguration>
{
    final DictionaryBuilder<URIDownloader> downloaders;

    LoadingConfigurationBuilder()
    {
        downloaders = DefaultDownloadersDictionary.get().thaw();
    }

    LoadingConfigurationBuilder(final LoadingConfiguration cfg)
    {
        downloaders = cfg.downloaders.thaw();
    }

    public LoadingConfigurationBuilder addScheme(final String scheme,
        final URIDownloader downloader)
    {
        downloaders.addEntry(checkScheme(scheme), downloader);
        return this;
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
            new URI(scheme, null, null);
        } catch (URISyntaxException ignored) {
            throw new LoadingConfigurationError(message.message(ILLEGAL_SCHEME)
                .put("scheme", scheme));
        }

        return scheme;
    }
}
