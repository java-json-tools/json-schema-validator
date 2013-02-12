package com.github.fge.jsonschema.load;

import com.github.fge.jsonschema.exceptions.unchecked.LoadingConfigurationError;
import com.github.fge.jsonschema.library.DictionaryBuilder;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.util.Thawed;

import java.net.URI;
import java.net.URISyntaxException;

import static com.github.fge.jsonschema.messages.LoadingMessages.*;

public final class LoadingConfigurationBuilder
    implements Thawed<LoadingConfiguration>
{
    private static final URI EMPTY_NAMESPACE = URI.create("#");

    final DictionaryBuilder<URIDownloader> downloaders;
    URI namespace;

    LoadingConfigurationBuilder()
    {
        downloaders = DefaultDownloadersDictionary.get().thaw();
        namespace = EMPTY_NAMESPACE;
    }

    LoadingConfigurationBuilder(final LoadingConfiguration cfg)
    {
        downloaders = cfg.downloaders.thaw();
        namespace = cfg.namespace;
    }

    public LoadingConfigurationBuilder addScheme(final String scheme,
        final URIDownloader downloader)
    {
        downloaders.addEntry(checkScheme(scheme), downloader);
        return this;
    }

    public LoadingConfigurationBuilder setNamespace(final String input)
    {
        namespace = checkRef(input);
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

    private static URI checkRef(final String input)
    {
        final ProcessingMessage message = new ProcessingMessage();

        if (input == null)
            throw new LoadingConfigurationError(message
                .message(NULL_NAMESPACE));

        final URI uri;
        try {
            uri = new URI(input);
        } catch (URISyntaxException ignored) {
            throw new LoadingConfigurationError(message.message(INVALID_URI)
                .put("input", input));
        }

        if (!JsonRef.fromURI(uri).isAbsolute())
            throw new LoadingConfigurationError(message
                .message(REF_NOT_ABSOLUTE).put("input", input));

        return uri;
    }
}
