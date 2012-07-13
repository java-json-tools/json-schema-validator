/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
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

package org.eel.kitchen.jsonschema.uri;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eel.kitchen.jsonschema.JsonSchemaException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class URIManager
{
    private final ObjectMapper mapper = new ObjectMapper()
        .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);

    private final Map<String, URIDownloader> downloaders
        = new HashMap<String, URIDownloader>();


    public URIManager()
    {
        downloaders.put("http", HTTPURIDownloader.getInstance());
    }

    public void registerDownloader(final String scheme,
        final URIDownloader downloader)
    {
        if (scheme == null)
            throw new IllegalArgumentException("scheme is null");

        if (scheme.isEmpty())
            throw new IllegalArgumentException("scheme is empty");

        try {
            new URI(scheme, "x", "y");
        } catch (URISyntaxException ignored) {
            throw new IllegalArgumentException("illegal scheme \"" + scheme
                + "\"");
        }

        if (downloaders.containsKey(scheme))
            throw new IllegalArgumentException("scheme \"" + scheme + "\" "
                + "already registered");

        downloaders.put(scheme, downloader);
    }

    public JsonNode getContent(final URI uri)
        throws JsonSchemaException
    {
        if (uri == null)
            throw new IllegalArgumentException("null URI");

        if (!uri.isAbsolute())
            throw new IllegalArgumentException("URI is not absolute");

        final String scheme = uri.getScheme();

        final URIDownloader downloader = downloaders.get(scheme);

        if (downloader == null)
            throw new JsonSchemaException("cannot handle scheme \"" + scheme
                + "\"");

        final InputStream in;

        try {
            in = downloader.fetch(uri);
        } catch (IOException e) {
            throw new JsonSchemaException("cannot fetch content from URI \""
                + uri + "\"", e);
        }

        try {
            return mapper.readTree(in);
        } catch (IOException e) {
            throw new JsonSchemaException("cannot read content from URI \""
                + uri + "\"", e);
        }
    }
}
