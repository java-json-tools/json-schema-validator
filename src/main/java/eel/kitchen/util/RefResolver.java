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

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class RefResolver
{
    private static final Pattern PATHSEP = Pattern.compile("/");
    private static final Pattern BEGINPATH = Pattern.compile("^/");

    private final ObjectMapper mapper = new ObjectMapper();
    private final JsonNode schema;

    private final Map<String, JsonNode> refs = new HashMap<String, JsonNode>();

    public RefResolver(final JsonNode schema)
    {
        this.schema = schema;
    }

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
