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
import com.github.fge.jsonschema.main.JsonSchemaException;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.ref.JsonRef;
import com.google.common.collect.Queues;

import java.util.Deque;

/**
 * Base implementation of a {@link JsonSchemaTree}
 */
public abstract class BaseJsonSchemaTree
    extends BaseJsonTree
    implements JsonSchemaTree
{
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

    /**
     * The main constructor
     *
     * @param loadingRef the loading reference
     * @param baseNode the base node
     */
    protected BaseJsonSchemaTree(final JsonRef loadingRef,
        final JsonNode baseNode)
    {
        super(baseNode);
        this.loadingRef = currentRef = loadingRef;

        final JsonRef ref = idFromNode(baseNode);

        if (ref != null)
            currentRef = currentRef.resolve(ref);

        startingRef = currentRef;
    }

    /**
     * Constructor for a schema tree loaded without a reference
     *
     * <p>This calls {@link #BaseJsonSchemaTree(JsonRef, JsonNode)} with an empty
     * reference as the loading reference.</p>
     *
     * @param baseNode the base node
     * @see JsonRef#emptyRef()
     */
    protected BaseJsonSchemaTree(final JsonNode baseNode)
    {
        this(JsonRef.emptyRef(), baseNode);
    }

    @Override
    public final void append(final String refToken)
    {
        append(JsonPointer.empty().append(refToken));
    }

    @Override
    public final void append(final int index)
    {
        append(JsonPointer.empty().append(index));
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

    @Override
    public final void setPointer(final JsonPointer pointer)
    {
        refStack.push(currentRef);
        currentRef = nextRef(startingRef, pointer.asElements(), baseNode);
        pushPointer(pointer);
        pushNode(pointer.resolve(baseNode));
    }

    @Override
    public final JsonRef resolve(final JsonRef other)
    {
        return currentRef.resolve(other);
    }

    @Override
    public final JsonRef getLoadingRef()
    {
        return loadingRef;
    }

    @Override
    public final JsonRef getCurrentRef()
    {
        return currentRef;
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

    @Override
    public final String toString()
    {
        return asJson().toString();
    }
}
