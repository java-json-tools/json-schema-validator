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
        super.append(refToken);
        refStack.push(currentRef);
        final JsonRef ref = idFromNode(currentNode);
        if (ref != null)
            currentRef = currentRef.resolve(ref);
    }

    @Override
    public final void append(final int index)
    {
        super.append(index);
        refStack.push(currentRef);
        final JsonRef ref = idFromNode(currentNode);
        if (ref != null)
            currentRef = currentRef.resolve(ref);
    }

    @Override
    public final void append(final JsonPointer ptr)
    {
        /*
         * We can push all old elements and set the new pwd right away. However,
         * we need to walk the nodes in order to correctly calculate the new URI
         * context.
         */
        dirStack.push(currentPointer);
        nodeStack.push(currentNode);
        refStack.push(currentRef);

        currentPointer = currentPointer.append(ptr);

        JsonRef nextRef = currentRef, id;
        JsonNode nextNode = currentNode;

        for (final JsonPointer p: ptr.asElements()) {
            nextNode = p.resolve(nextNode);
            id = idFromNode(nextNode);
            if (id != null)
                nextRef = nextRef.resolve(id);
        }

        currentRef = nextRef;
        currentNode = nextNode;
    }

    @Override
    public final void pop()
    {
        currentRef = refStack.pop();
        super.pop();
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

    @Override
    public final String toString()
    {
        return asJson().toString();
    }
}
