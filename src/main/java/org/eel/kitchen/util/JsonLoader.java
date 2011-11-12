/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.util;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

/**
 * Utility class to load JSON documents (schemas or instance) from various
 * sources as {@link JsonNode}s.
 *
 * <p>It should be noted here that the {@link ObjectMapper} used to read
 * everything has the following features enabled:</p>
 * <ul>
 *     <li>{@link DeserializationConfig.Feature#USE_BIG_DECIMAL_FOR_FLOATS};</li>
 *     <li>{@link DeserializationConfig.Feature#USE_BIG_INTEGER_FOR_INTS}.</li>
 * </ul>
 * <p>This is to be able to deal with arbitrary long numbers. Otherwise
 * Jackson limits itself to double numbers, for performance reason but also
 * because, to quote its documentation, "Javascript standard specifies that
 * all number handling should be done using 64-bit IEEE 754 floating point
 * values" (therefore the equivalent of the {@code double} primitive type).
 * </p>
 */
public final class JsonLoader
{
    /**
     * The mapper which does everything behind the scenes...
     */
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        /**
         * NECESSARY! Otherwise Jackson will limit itself to what Javascript
         * can operate!
         */
        mapper.enable(DeserializationConfig.Feature.USE_BIG_DECIMAL_FOR_FLOATS);
        mapper.enable(DeserializationConfig.Feature.USE_BIG_INTEGER_FOR_INTS);
    }

    /**
     * A shortcut: myself as a {@link Class} object.
     */
    private static final Class<JsonLoader> myself = JsonLoader.class;

    /**
     * Read a {@link JsonNode} from a resource path. Explicitly throws an
     * {@link IOException} if the resource is null, instead of letting a
     * {@link NullPointerException} slip through...
     *
     * @param resource The path to the resource
     * @return the JSON document at the resource
     * @throws IOException if the resource does not exist or there was a
     * problem loading it, or if the JSON document is invalid
     */
    public static JsonNode fromResource(final String resource)
        throws IOException
    {
        final String realResource = resource.replaceFirst("^(?!/)", "/");

        final JsonNode ret;

        final InputStream in = myself.getResourceAsStream(realResource);

        if (in == null)
            throw new IOException("resource " + resource + " not found");

        try {
            ret = mapper.readTree(in);
        } finally {
            in.close();
        }

        return ret;
    }

    /**
     * Read a {@link JsonNode} from an URL.
     *
     * @param url The URL to fetch the JSON document from
     * @return The document at that URL
     * @throws IOException in case of network problems etc.
     */
    public static JsonNode fromURL(final URL url)
        throws IOException
    {
        return mapper.readTree(url);
    }

    /**
     * Read a {@link JsonNode} from a file on the local filesystem.
     *
     * @param path the path (relative or absolute) to the file
     * @return the document in the file
     * @throws IOException if this is not a file, if it cannot be read, etc.
     */
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

    /**
     * Same as #fromPath, but this time the user supplies the {@link File}
     * object instead
     *
     * @param file the File object
     * @return The document
     * @throws IOException in many cases!
     */
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

    /**
     * Read a {@link JsonNode} from a user supplied {@link Reader}
     *
     * @param reader The reader
     * @return the document
     * @throws IOException if the reader has problems
     */
    public static JsonNode fromReader(final Reader reader)
        throws IOException
    {
        return mapper.readTree(reader);
    }
}
