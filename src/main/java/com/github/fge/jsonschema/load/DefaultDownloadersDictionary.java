package com.github.fge.jsonschema.load;

import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.library.DictionaryBuilder;

/**
 * Dictionary of default supported URI schemes
 *
 * <p>The set of default supported schemes is:</p>
 *
 * <ul>
 *     <li>{@code http}</li>;
 *     <li>{@code https}</li>;
 *     <li>{@code file}</li>;
 *     <li>{@code ftp}</li>;
 *     <li>{@code jar}</li>;
 *     <li>{@code resource}</li>.
 * </ul>
 *
 * @see DefaultURIDownloader
 * @see ResourceURIDownloader
 */
public final class DefaultDownloadersDictionary
{
    private static final Dictionary<URIDownloader> DICTIONARY;

    private DefaultDownloadersDictionary()
    {
    }

    static {
        final DictionaryBuilder<URIDownloader> builder
            = Dictionary.newBuilder();

        String scheme;
        URIDownloader downloader;

        scheme = "http";
        downloader = DefaultURIDownloader.getInstance();
        builder.addEntry(scheme, downloader);

        scheme = "https";
        downloader = DefaultURIDownloader.getInstance();
        builder.addEntry(scheme, downloader);

        scheme = "file";
        downloader = DefaultURIDownloader.getInstance();
        builder.addEntry(scheme, downloader);

        scheme = "ftp";
        downloader = DefaultURIDownloader.getInstance();
        builder.addEntry(scheme, downloader);

        scheme = "jar";
        downloader = DefaultURIDownloader.getInstance();
        builder.addEntry(scheme, downloader);

        scheme = "resource";
        downloader = ResourceURIDownloader.getInstance();
        builder.addEntry(scheme, downloader);

        DICTIONARY = builder.freeze();
    }

    /**
     * Get the dictionary of downloaders
     *
     * @return a dictionary
     */
    public static Dictionary<URIDownloader> get()
    {
        return DICTIONARY;
    }
}
