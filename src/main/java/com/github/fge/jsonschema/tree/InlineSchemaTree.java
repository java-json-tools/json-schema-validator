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
import com.fasterxml.jackson.databind.node.MissingNode;
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
    private final Map<JsonRef, JsonPointer> ptrRefs;
    private final Map<JsonRef, JsonPointer> otherRefs;

    public InlineSchemaTree(final JsonRef loadingRef, final JsonNode baseNode)
    {
        super(loadingRef, baseNode);

        final Map<JsonRef, JsonPointer> ptrMap = Maps.newHashMap();
        final Map<JsonRef, JsonPointer> otherMap = Maps.newHashMap();
        walk(currentRef, currentNode, JsonPointer.empty(), ptrMap, otherMap);
        ptrRefs = ImmutableMap.copyOf(ptrMap);
        otherRefs = ImmutableMap.copyOf(otherMap);
    }

    public InlineSchemaTree(final JsonNode baseNode)
    {
        this(JsonRef.emptyRef(), baseNode);
    }

    @Override
    public boolean contains(final JsonRef ref)
    {
        return matchingPointer(ref) != null;
    }

    @Override
    public JsonNode retrieve(final JsonRef ref)
    {
        final JsonPointer ptr = matchingPointer(ref);
        return ptr == null ? MissingNode.getInstance() : ptr.resolve(baseNode);
    }

    private JsonPointer matchingPointer(final JsonRef ref)
    {
        return ref.getFragment().isPointer()
            ? refMatchingPointer(ref)
            : otherMatchingPointer(ref);
    }

    // Called when the target ref is JSON Pointer terminated
    private JsonPointer refMatchingPointer(final JsonRef ref)
    {
        final JsonPointer refPtr = (JsonPointer) ref.getFragment();

        // Note: we are guaranteed that loadingRef has an empty fragment
        if (loadingRef.contains(ref))
            return refPtr;

        JsonRef inlineRef;
        JsonPointer inlinePtr;

        for (final Map.Entry<JsonRef, JsonPointer> entry: ptrRefs.entrySet()) {
            inlineRef = entry.getKey();
            if (!entry.getKey().contains(ref))
                continue;
            inlinePtr = (JsonPointer) inlineRef.getFragment();
            if (!inlinePtr.isParentOf(refPtr))
                continue;
            return entry.getValue().append(inlinePtr.relativize(refPtr));
        }

        return null;
    }

    // Called when the target ref is not JSON Pointer terminated
    private JsonPointer otherMatchingPointer(final JsonRef ref)
    {
        return otherRefs.get(ref);
    }

    private static void walk(final JsonRef baseRef, final JsonNode node,
        final JsonPointer ptr, final Map<JsonRef, JsonPointer> ptrMap,
        final Map<JsonRef, JsonPointer> otherMap)
    {
        if (!node.isObject())
            return;

        final JsonRef ref = idFromNode(node);
        final Map<JsonRef, JsonPointer> targetMap;

        JsonRef nextRef = baseRef;

        if (ref != null) {
            nextRef = baseRef.resolve(ref);
            targetMap = nextRef.getFragment().isPointer() ? ptrMap : otherMap;
            targetMap.put(nextRef, ptr);
        }

        final Map<String, JsonNode> tmp = JacksonUtils.asMap(node);

        for (final Map.Entry<String, JsonNode> entry: tmp.entrySet())
            walk(nextRef, entry.getValue(), ptr.append(entry.getKey()), ptrMap,
                otherMap);
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
