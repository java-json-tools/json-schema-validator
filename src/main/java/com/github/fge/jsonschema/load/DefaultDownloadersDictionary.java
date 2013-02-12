package com.github.fge.jsonschema.load;

import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.library.DictionaryBuilder;

public final class DefaultDownloadersDictionary
{
    private static final Dictionary<URIDownloader> DICTIONARY;

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

    public static Dictionary<URIDownloader> get()
    {
        return DICTIONARY;
    }
}
