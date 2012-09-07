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
import org.eel.kitchen.jsonschema.ref.JsonPointer;

public final class NodeAndPath
{
    private static final NodeAndPath MISSING
        = new NodeAndPath(MissingNode.getInstance(), JsonPointer.empty(),
        false);

    private final JsonNode node;
    private final JsonPointer path;
    private final boolean computed;

    public static NodeAndPath forNode(final JsonNode node)
    {
        return new NodeAndPath(node, JsonPointer.empty(), false);
    }

    public static NodeAndPath missing()
    {
        return MISSING;
    }

    public NodeAndPath(final JsonNode node, final JsonPointer path)
    {
        this(node, path, false);
    }

    public NodeAndPath(final JsonNode node, final JsonPointer path,
        final boolean computed)
    {
        this.node = node;
        this.path = path;
        this.computed = computed;
    }

    public JsonNode getNode()
    {
        return node;
    }

    public JsonPointer getPath()
    {
        return path;
    }

    public boolean isComputed()
    {
        return computed;
    }

    public boolean isMissing()
    {
        return this == MISSING;
    }

    @Override
    public String toString()
    {
        return "path: " + path + "; computed: " + computed
            + "; node: " + node;
    }
}
