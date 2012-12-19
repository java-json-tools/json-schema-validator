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

package org.eel.kitchen.jsonschema.util.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;

/**
 * <p>A small set of utility methods over Jackson.</p>
 */

public final class JacksonUtils
{
    private JacksonUtils()
    {
    }

    /**
     * Return a map out of an object instance
     *
     * @param node the node
     * @return a mutable map made of the instance's entries
     */
    public static Map<String, JsonNode> nodeToMap(final JsonNode node)
    {
        final Map<String, JsonNode> ret = Maps.newHashMap();

        final Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();

        Map.Entry<String, JsonNode> entry;
        while (iterator.hasNext()) {
            entry = iterator.next();
            ret.put(entry.getKey(), entry.getValue());
        }

        return ret;
    }
}
