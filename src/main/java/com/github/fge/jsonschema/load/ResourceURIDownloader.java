/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.fge.jsonschema.load;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * A downloader for the custom {@code resource} "scheme"
 *
 * <p>Here, {@code resource} is to be interpreted as a Java resource, exactly
 * what you would obtain using {@link Class#getResourceAsStream(String)}.</p>
 *
 * <p>And in fact, this is what this downloader does: it takes whatever is in
 * the provided URI's path (using {@link URI#getPath()}) and tries to make an
 * input stream of it. The difference is that an {@link IOException} will be
 * thrown if the resource cannot be found (instead of returning {@code null}).
 * </p>
 */
public final class ResourceURIDownloader
    implements URIDownloader
{
    private static final Class<ResourceURIDownloader> MYSELF
        = ResourceURIDownloader.class;

    private static final URIDownloader INSTANCE = new ResourceURIDownloader();

    private ResourceURIDownloader()
    {
    }

    public static URIDownloader getInstance()
    {
        return INSTANCE;
    }

    @Override
    public InputStream fetch(final URI source)
        throws IOException
    {
        final String resource = source.getPath();
        final InputStream in = MYSELF.getResourceAsStream(resource);

        if (in == null)
            throw new IOException("resource " + resource + " not found");

        return in;
    }
}
