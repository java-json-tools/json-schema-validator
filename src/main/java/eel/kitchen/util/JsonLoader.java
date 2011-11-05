/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.util;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

public final class JsonLoader
{
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final int SLASH = (int) '/';

    private static final Class<JsonLoader> myself = JsonLoader.class;

    public static JsonNode fromResource(final String resource)
        throws IOException
    {
        String realResource = resource;

        if ((int) realResource.charAt(0) != SLASH)
            realResource = '/' + realResource;

        final JsonNode ret;

        final InputStream in = myself.getResourceAsStream(realResource);

        try {
            ret = mapper.readTree(in);
        } finally {
            in.close();
        }

        return ret;
    }

    public static JsonNode fromURL(final URL url)
        throws IOException
    {
        return mapper.readTree(url);
    }

    public static JsonNode fromPath(final String path)
        throws IOException
    {
        final JsonNode ret;

        final FileInputStream in = new FileInputStream(path);

        try {
            ret = mapper.readTree(in);
        } finally {
            in.close();
        }

        return ret;
    }

    public static JsonNode fromFile(final File file)
        throws IOException
    {
        final JsonNode ret;

        final FileInputStream in = new FileInputStream(file);
        try {
            ret = mapper.readTree(in);
        } finally {
            in.close();
        }

        return ret;
    }

    public static JsonNode fromReader(final Reader reader)
        throws IOException
    {
        return mapper.readTree(reader);
    }
}
