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
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * A {@link JsonSchemaTree} using inline dereferencing
 *
 * <p>When using inline dereferencing, a (fully resolved) reference is contained
 * within the tree if it is relative to one of the URI resolution contexts in
 * this tree, or to the loading URI itself.</p>
 *
 * <p>And this makes things quite hairy to handle. Consider, for instance, that
 * {@code foo://bar#/a/b/c} is contained within such a tree if the tree has a
 * resolution context with URI {@code foo://bar#/a/b}!</p>
 *
 * <p>This implementaiton correctly handles this kind of corner cases. For
 * resolution contexts whose fragment part is not a JSON Pointer, an exact
 * reference equality is required.</p>
 */
public final class InlineSchemaTree
    extends JsonSchemaTree
{
    /**
     * The list of contexts whose URIs bear a JSON Pointer as a fragment part
     */
    private final Map<JsonRef, JsonPointer> ptrRefs;

    /**
     * The list of contexts whose URIs bear a non JSON Pointer fragment part
     */
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

    /**
     * Tell whether a full resolved reference is contained within this tree
     *
     * <p>Given the pecularity of inline dereferencing, this operation is not
     * as simple as for canonical trees. The algorithm is as follows:</p>
     *
     * <ul>
     *     <li>if the fragment part of the target reference is not a JSON
     *     Pointer, only non JSON Pointer resolution contexts are considered,
     *     and return {@code true} if and only if an exact match is found,
     *     {@code false} otherwise;</li>
     *     <li>at this point, we know the fragment part is a JSON Pointer;
     *     compare the reference without a fragment part to all "JSON Pointer
     *     enabled" resolution contexts, without a fragment part: if there is
     *     no match, return {@code false};</li>
     *     <li>on a match, return {@code true} if the JSON Pointer of the
     *     matched context is a parent of the ref's JSON Pointer.</li>
     * </ul>
     *
     * @param ref the target reference
     * @return see description
     * @see JsonPointer#isParentOf(JsonPointer)
     */
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

    /**
     * Return a matching pointer in this tree for a fully resolved reference
     *
     * <p>Depending on whether the reference's fragment is a JSON Pointer,
     * this will call either {@link #refMatchingPointer(JsonRef)} or {@link
     * #otherMatchingPointer(JsonRef)}.</p>
     *
     * @param ref the reference
     * @return the matching pointer, or {@code null} if not found
     */
    private JsonPointer matchingPointer(final JsonRef ref)
    {
        return ref.getFragment().isPointer()
            ? refMatchingPointer(ref)
            : otherMatchingPointer(ref);
    }

    /**
     * Return a matching pointer for a JSON Pointer terminated fully resolved
     * reference
     *
     * <p>This includes the loading URI.</p>
     *
     * @param ref the target reference
     * @return the matching pointer, or {@code null} if not found
     */
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

    /**
     * Return a matching pointer for a non JSON Pointer terminated, fully
     * resolved reference
     *
     * <p>This simply tries and retrieves a value from {@link #otherRefs},
     * since an exact matching is required in such a case.</p>
     *
     * @param ref the target reference
     * @return the matching pointer, or {@code null} if not found
     */
    private JsonPointer otherMatchingPointer(final JsonRef ref)
    {
        return otherRefs.get(ref);
    }

    /**
     * Walk a JSON document to collect URI contexts
     *
     * <p>Unlike what happens with a canonical schema tree, we <i>must</i> walk
     * the whole tree in advance here. This is necessary for {@link
     * #contains(JsonRef)} and {@link #retrieve(JsonRef)} to work.</p>
     *
     * <p>This method is called recursively. Its furst invocation is from the
     * constructor, with {@link #loadingRef} as a reference, {@link #baseNode}
     * as a JSON document and an empty pointer as the document pointer.</p>
     *
     * @param baseRef the current context
     * @param node the current document
     * @param ptr the current pointer into the base document
     * @param ptrMap a "JSON Pointer context" map to fill
     * @param otherMap a non JSON Pointer context map to fill
     *
     * @see #idFromNode(JsonNode)
     */
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
}
