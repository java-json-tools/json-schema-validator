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
import com.github.fge.jsonschema.load.Dereferencing;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.google.common.collect.Maps;

import java.util.Map;

public final class InlineSchemaTree
    extends BaseSchemaTree
{
    /**
     * The list of contexts whose URIs bear a JSON Pointer as a fragment part
     */
    private final Map<JsonRef, JsonPointer> ptrRefs = Maps.newHashMap();

    /**
     * The list of contexts whose URIs bear a non JSON Pointer fragment part
     */
    private final Map<JsonRef, JsonPointer> otherRefs = Maps.newHashMap();

    public InlineSchemaTree(final JsonNode baseNode)
    {
        this(JsonRef.emptyRef(), baseNode);
    }

    public InlineSchemaTree(final JsonRef loadingRef, final JsonNode baseNode)
    {
        this(loadingRef, baseNode, JsonPointer.empty(), false);
    }

    private InlineSchemaTree(final JsonRef loadingRef, final JsonNode baseNode,
        final JsonPointer pointer, final boolean valid)
    {
        super(loadingRef, baseNode, pointer, Dereferencing.INLINE, valid);
        walk(currentRef, node, JsonPointer.empty(), ptrRefs, otherRefs);
    }

    @Override
    public SchemaTree append(final JsonPointer pointer)
    {
        return new InlineSchemaTree(loadingRef, baseNode,
            this.pointer.append(pointer), valid);
    }

    @Override
    public SchemaTree setPointer(final JsonPointer pointer)
    {

        return new InlineSchemaTree(loadingRef, baseNode, pointer, valid);
    }

    @Override
    public boolean containsRef(final JsonRef ref)
    {
        return getMatchingPointer(ref) != null;
    }

    @Override
    public JsonPointer matchingPointer(final JsonRef ref)
    {
        final JsonPointer ret = getMatchingPointer(ref);
        if (ret == null)
            return null;

        return ret.resolve(baseNode).isMissingNode() ? null : ret;
    }

    @Override
    public SchemaTree withValidationStatus(final boolean valid)
    {
        return new InlineSchemaTree(loadingRef, baseNode, pointer, valid);
    }

    private JsonPointer getMatchingPointer(final JsonRef ref)
    {
        return ref.getFragment().isPointer()
            ? refMatchingPointer(ref)
            : otherMatchingPointer(ref);
    }

    /**
     * Return a matching pointer for a JSON Pointer terminated fully resolved
     * reference
     *
     * <p>This includes the loading URI. Note, however, that due to "id"
     * intricacies, the test against the loading reference is done only as a
     * last resort.</p>
     *
     * @param ref the target reference
     * @return the matching pointer, or {@code null} if not found
     */
    private JsonPointer refMatchingPointer(final JsonRef ref)
    {
        final JsonPointer refPtr = (JsonPointer) ref.getFragment();

        /*
         * When using inline addressing, we must favor whatever "id" has defined
         * as a URI scope over what the loading URI is...
         */

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

        /*
         * ... Which means this test must be done last... (since refPtr is
         * declared final, this is safe)
         */
        return loadingRef.contains(ref) ? refPtr : null;
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
     * #containsRef(JsonRef)} and {@link #matchingPointer(JsonRef)} to work.</p>
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
        /*
         * FIXME: this means we won't go through schemas in keywords such as
         * "anyOf" and friends. No idea whether this is a concern. It may be.
         */
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
