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

package org.eel.kitchen.jsonschema.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;

public final class NodeAndPath
{
    private static final NodeAndPath MISSING
        = new NodeAndPath(MissingNode.getInstance(), "N/A");

    private final JsonNode node;
    private final String path;

    public static NodeAndPath forNode(final JsonNode node)
    {
        return new NodeAndPath(node, "");
    }

    public static NodeAndPath missing()
    {
        return MISSING;
    }

    public NodeAndPath(final JsonNode node, final String path)
    {
        this.node = node;
        this.path = path;
    }

    public JsonNode getNode()
    {
        return node;
    }

    public String getPath()
    {
        return path;
    }

    public boolean isMissing()
    {
        return this == MISSING;
    }
}
