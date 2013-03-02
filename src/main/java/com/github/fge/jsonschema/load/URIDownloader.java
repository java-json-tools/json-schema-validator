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
 * URI downloader for a given scheme
 */
// TODO REMOVE
public interface URIDownloader
{
    /**
     * Fetch the content at a given URI
     *
     * @param source the URI
     * @return an {@link InputStream}
     * @throws IOException unable to find an input stream
     */
    InputStream fetch(final URI source)
        throws IOException;
}
