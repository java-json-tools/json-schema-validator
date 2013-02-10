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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.main.JsonSchemaException;
import com.github.fge.jsonschema.processing.ref.Dereferencing;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.ref.JsonRef;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

import java.util.Deque;
import java.util.Set;

/**
 * A {@link JsonTree} carrying URI resolution context information
 *
 * <p>In addition to what {@link JsonTree} does, this tree also modifies URI
 * resolution context information when changing paths, and adds methods in order
 * to query this resolution context.</p>
 *
 * <p>All context information is carried as JSON References, since this is what
 * is used for addressing in JSON Schema.</p>
 *
 * @see JsonRef
 * @see CanonicalSchemaTree
 * @see InlineSchemaTree
 */
public abstract class JsonSchemaTree
    extends BaseJsonTree
{
    /**
     * Whether inline dereferencing is used
     */
    private final Dereferencing dereferencing;

    /**
     * The JSON Reference from which this node has been loaded
     *
     * <p>If loaded without a URI, this will be the empty reference.</p>
     */
    protected final JsonRef loadingRef;

    /**
     * The JSON Reference representing the context at the root of the schema
     *
     * <p>It will defer from {@link #loadingRef} if there is an {@code id} at
     * the top level.</p>
     */
    protected final JsonRef startingRef;

    /**
     * The stack of resolution contexts
     */
    protected final Deque<JsonRef> refStack = Queues.newArrayDeque();

    /**
     * The current resolution context
     */
    protected JsonRef currentRef;

    private final Set<JsonPointer> validatedPaths = Sets.newHashSet();

    private final Set<JsonPointer> uncheckedPaths
        = Sets.newHashSet(JsonPointer.empty());

    private boolean valid = false;

    /**
     * The main constructor
     *
     * @param loadingRef the loading reference
     * @param baseNode the base node
     */
    protected JsonSchemaTree(final JsonRef loadingRef, final JsonNode baseNode,
        final Dereferencing dereferencing)
    {
        super(baseNode);
        this.dereferencing = dereferencing;
        this.loadingRef = currentRef = loadingRef;

        final JsonRef ref = idFromNode(baseNode);

        if (ref != null)
            currentRef = currentRef.resolve(ref);

        startingRef = currentRef;
    }

    @Override
    public final void append(final JsonPointer ptr)
    {
        refStack.push(currentRef);
        currentRef = nextRef(currentRef, ptr.asElements(), currentNode);
        pushPointer(currentPointer.append(ptr));
        pushNode(ptr.resolve(currentNode));
    }

    @Override
    public final void pop()
    {
        currentRef = refStack.pop();
        popPointer();
        popNode();
    }

    public final void setPointer(final JsonPointer pointer)
    {
        refStack.push(currentRef);
        currentRef = nextRef(startingRef, pointer.asElements(), baseNode);
        pushPointer(pointer);
        pushNode(pointer.resolve(baseNode));
    }

    /**
     * Resolve a JSON Reference against the current resolution context
     *
     * @param other the JSON Reference to resolve
     * @return the resolved reference
     * @see JsonRef#resolve(JsonRef)
     */
    public final JsonRef resolve(final JsonRef other)
    {
        return currentRef.resolve(other);
    }

    /**
     * Tell whether a JSON Reference is contained within this schema tree
     *
     * <p>This method will return {@code true} if the caller can <i>attempt</i>
     * to retrieve the JSON value addressed by this reference from the schema
     * tree directly.</p>
     *
     * <p>Note that the reference <b>must</b> be fully resolved for this method
     * to work.</p>
     *
     * @param ref the target reference
     * @return see description
     * @see #resolve(JsonRef)
     */
    public abstract boolean containsRef(final JsonRef ref);

    /**
     * Return a matching pointer in this tree for a fully resolved reference
     *
     * <p>This must be called <b>only</b> when {@link #containsRef(JsonRef)}
     * returns {@code true}. Otherwise, its result is undefined.</p>
     *
     * @param ref the reference
     * @return the matching pointer, or {@code null} if not found
     */
    public abstract JsonPointer matchingPointer(final JsonRef ref);

    /**
     * Get the loading URI for that schema
     *
     * @return the loading URI as a {@link JsonRef}
     */
    public final JsonRef getLoadingRef()
    {
        return loadingRef;
    }

    /**
     * Get the current resolution context
     *
     * @return the context as a {@link JsonRef}
     */
    public final JsonRef getCurrentRef()
    {
        return currentRef;
    }

    public final boolean isValidated()
    {
        final JsonPointer closestUnchecked
            = closestPointer(uncheckedPaths, currentPointer);
        final JsonPointer closestValidated
            = closestPointer(validatedPaths, currentPointer);

        if (closestValidated == null)
            return false;
        if (closestUnchecked == null)
            return true;
        return closestUnchecked.isParentOf(closestValidated);
    }

    public final void addValidatedPath(final JsonPointer pointer)
    {
        validatedPaths.add(pointer);
        uncheckedPaths.remove(pointer);
    }

    public final void addUncheckedPath(final JsonPointer pointer)
    {
        uncheckedPaths.add(pointer);
    }

    public final void setValid(final boolean valid)
    {
        this.valid = valid;
    }

    /**
     * Return a copy of this tree at its current state but with an empty stack
     *
     * @return the copy
     */
    public final JsonSchemaTree copy()
    {
        final JsonSchemaTree ret = dereferencing.newTree(loadingRef, baseNode);

        ret.currentRef = currentRef;
        ret.currentPointer = currentPointer;
        ret.currentNode = currentNode;
        ret.validatedPaths.addAll(validatedPaths);
        ret.uncheckedPaths.addAll(uncheckedPaths);
        ret.valid = valid;
        return ret;
    }

    /**
     * Build a JSON Reference from a node
     *
     * <p>This will return {@code null} if the reference could not be built. The
     * conditions for a successful build are as follows:</p>
     *
     * <ul>
     *     <li>the node is an object;</li>
     *     <li>it has a member named {@code id};</li>
     *     <li>the value of this member is a string;</li>
     *     <li>this string is a valid URI.</li>
     * </ul>
     *
     * @param node the node
     * @return a JSON Reference, or {@code null}
     */
    protected static JsonRef idFromNode(final JsonNode node)
    {
        if (!node.path("id").isTextual())
            return null;

        try {
            return JsonRef.fromString(node.get("id").textValue());
        } catch (JsonSchemaException ignored) {
            return null;
        }
    }

    /**
     * Calculate the next URI context from a starting reference and node
     *
     * @param startingRef the starting reference
     * @param pointers the list of JSON Pointers
     * @param startingNode the starting node
     * @return the calculated reference
     */
    private static JsonRef nextRef(final JsonRef startingRef,
        final Iterable<JsonPointer> pointers, final JsonNode startingNode)
    {
        JsonRef ret = startingRef;
        JsonRef idRef;
        JsonNode node = startingNode;

        for (final JsonPointer pointer: pointers) {
            node = pointer.resolve(node);
            idRef = idFromNode(node);
            if (idRef != null)
                ret = ret.resolve(idRef);
        }

        return ret;
    }

    private static JsonPointer closestPointer(final Set<JsonPointer> set,
        final JsonPointer pointer)
    {
        JsonPointer ret = null;
        for (final JsonPointer ptr: set) {
            if (!ptr.isParentOf(pointer))
                continue;
            if (ret == null)
                ret = ptr;
            else if (ret.isParentOf(ptr))
                ret = ptr;
        }

        return ret;
    }

    @Override
    public final JsonNode asJson()
    {
        final ObjectNode ret = FACTORY.objectNode();

        ret.put("loadingURI", FACTORY.textNode(loadingRef.toString()));
        ret.put("pointer", FACTORY.textNode(currentPointer.toString()));
        ret.put("currentContext", FACTORY.textNode(currentRef.toString()));
        ret.put("dereferencing", FACTORY.textNode(dereferencing.toString()));

        return ret;
    }

    @Override
    public final String toString()
    {
        return asJson().toString();
    }
}
