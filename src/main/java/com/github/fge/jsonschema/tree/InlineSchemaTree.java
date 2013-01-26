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

package com.github.fge.jsonschema.tree;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.ref.JsonFragment;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

public final class InlineSchemaTree
    extends JsonSchemaTree
{
    private final Map<JsonRef, JsonPointer> inlineRefs;

    public InlineSchemaTree(final JsonRef loadingRef, final JsonNode baseNode)
    {
        super(loadingRef, baseNode);

        final Map<JsonRef, JsonPointer> map = Maps.newHashMap();
        walk(currentRef, currentNode, JsonPointer.empty(), map);
        inlineRefs = ImmutableMap.copyOf(map);
    }

    public InlineSchemaTree(final JsonNode baseNode)
    {
        this(JsonRef.emptyRef(), baseNode);
    }

    @Override
    public boolean contains(final JsonRef ref)
    {
        if (loadingRef.contains(ref))
            return true;

        for (final JsonRef inlineRef: inlineRefs.keySet())
            if (refContains(inlineRef, ref))
                return true;

        return false;
    }

    private static void walk(final JsonRef baseRef, final JsonNode node,
        final JsonPointer ptr, final Map<JsonRef, JsonPointer> map)
    {
        if (!node.isObject())
            return;

        JsonRef nextRef = baseRef;
        final JsonRef ref = idFromNode(node);

        if (ref != null) {
            nextRef = baseRef.resolve(ref);
            map.put(nextRef, ptr);
        }

        final Map<String, JsonNode> tmp = JacksonUtils.asMap(node);

        for (final Map.Entry<String, JsonNode> entry: tmp.entrySet())
            walk(nextRef, entry.getValue(), ptr.append(entry.getKey()), map);
    }

    private static boolean refContains(final JsonRef inlineRef,
        final JsonRef ref)
    {
        /*
         * No need to check further if they don't have the same locator
         */
        if (!inlineRef.contains(ref))
            return false;

        /*
         * Compare fragments
         */
        final JsonFragment inlineFragment = inlineRef.getFragment();
        final JsonFragment refFragment = ref.getFragment();

        /*
         * If equal, match
         */
        if (inlineFragment.equals(refFragment))
            return true;

        /*
         * If none of them is a pointer, no match
         */
        if (!(inlineFragment.isPointer() && refFragment.isPointer()))
            return false;

        return ((JsonPointer) inlineFragment)
            .isParentOf((JsonPointer) refFragment);
    }
}
