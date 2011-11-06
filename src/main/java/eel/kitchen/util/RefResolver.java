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
import org.codehaus.jackson.node.MissingNode;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <p>A class dedicated to handle $ref (section 5.28). The support is,
 * as of now, limited to:</p>
 * <ul>
 *     <li>direct inline references (using JSON paths, like, for instance,
 *     {@code #/path/within/document};
 *     </li>
 *     <li>URLs.</li>
 * </ul>
 * <p>It is up to the caller to handle exceptions (well, only one,
 * really: {@code IOException}) returned by this class if it fails to resolve
 * the reference. Note that failing to lookup a path inside a document which
 * has been acquired successfully will not raise an exception,
 * but return a {@link MissingNode}.
 * </p>
 */
public final class RefResolver
{
    /**
     * Pattern to recognize a JSON path separator
     */
    private static final Pattern PATHSEP = Pattern.compile("/");

    /**
     * Pattern to recognize a / at the beginning of a JSON path
     */
    private static final Pattern BEGINPATH = Pattern.compile("^/");

    /**
     * Schema used by that RefResolver for "inline refs" (ie,
     * {@code #/path/within/document}
     */
    private final JsonNode schema;

    /**
     * Cache of "external refs" (acquired via HTTP only for now). Note that
     * keys are strings, not {@link URL}s: calling {@link URL#equals(Object)}
     * on URLs can raise DNS lookups and we don't want that!
     */
    private final Map<String, JsonNode> refs = new HashMap<String, JsonNode>();

    /**
     * The only constructor.
     *
     * @param schema the schema to which inline refs apply
     */
    public RefResolver(final JsonNode schema)
    {
        this.schema = schema;
    }

    /**
     * Resolve a path given by a {@code $ref}. This method starts by
     * separating the URL part from the fragment part (resp. what is before
     * the {@code #} and what is after it), then tries and fetches the
     * document if the URL part is not empty, before calling {@link
     * #resolvePath(JsonNode, String)} on the acquired document.
     *
     * @param s The string representing the path
     * @return The corresponding {@link JsonNode},
     * which is a {@link MissingNode} if the path matches nothing
     * @throws IOException the document pointed to by the URL part could not
     * be downloaded, or it is invalid
     */
    public JsonNode resolve(final String s)
        throws IOException
    {
        final int fragmentIndex = s.indexOf('#');

        final URI uri;
        final String addr, fragment;

        if (fragmentIndex == -1) {
            addr = s;
            fragment = "";
        } else {
            addr = s.substring(0, fragmentIndex);
            fragment = s.substring(fragmentIndex + 1, s.length());
        }

        uri = URI.create(addr);

        JsonNode ret = schema;

        if (uri.getScheme() != null) {
            if (refs.containsKey(addr))
                ret = refs.get(addr);
            else {
                ret = JsonLoader.fromURL(uri.toURL());
                refs.put(addr, ret);
            }
        }

        return resolvePath(ret, fragment);
    }

    /**
     * Resolve a JSON path within a document
     *
     * @param schema the document to which the JSON path applies
     * @param path the path
     * @return the {@link JsonNode} corresponding to that path (a {@link
     * MissingNode} if the path matches nothing
     */
    private static JsonNode resolvePath(final JsonNode schema,
        final String path)
    {
        if (path == null || "".equals(path))
            return schema;

        final String s = BEGINPATH.matcher(path).replaceFirst("");

        JsonNode ret = schema;

        for (final String element: PATHSEP.split(s))
            ret = ret.path(element);

        return ret;
    }
}
