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
import com.github.fge.jsonschema.main.JsonSchemaException;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.ref.JsonRef;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Queues;

import java.util.Deque;

/**
 * A {@link JsonTree} carrying URI resolution context information
 *
 * <p>In addition to what {@link JsonTree} does, this (abstract) class also
 * carries URI resolution context information when changing the pointer into
 * the node, and includes additional methods related to that resolution
 * context.</p>
 *
 * <p>The URI context information carries not only the URI from which the node
 * has been loaded, but also any encounters of the {@code id} keyword along the
 * way. When you {@code append()} or {@code pop()}, the resolution context will
 * change accordingly.</p>
 *
 * <p>All context information is carried as JSON References, since this is what
 * is used for addressing in JSON Schema.</p>
 *
 * @see JsonRef
 */
public abstract class JsonSchemaTree
    extends JsonTree
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
     * Protected constructor
     *
     * @param loadingRef the loading reference
     * @param baseNode the base node
     */
    protected JsonSchemaTree(final JsonRef loadingRef, final JsonNode baseNode)
    {
        super(baseNode);
        this.loadingRef = currentRef = loadingRef;

        final JsonRef ref = idFromNode(baseNode);

        if (ref != null)
            currentRef = currentRef.resolve(ref);
    }

    /**
     * Protected constructor for a schema tree loaded without a reference
     *
     * @param baseNode the base node
     */
    protected JsonSchemaTree(final JsonNode baseNode)
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
        dirStack.push(pwd);
        nodeStack.push(currentNode);
        refStack.push(currentRef);

        pwd = pwd.append(ptr);

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
    public abstract boolean contains(final JsonRef ref);

    /**
     * Try and retrieve a node in this tree from a JSON Reference
     *
     * <p>This method must be called if and only if {@link #contains(JsonRef)}
     * returns {@code true} for this reference.</p>
     *
     * <p>This method is <i>not</i> guaranteed to succeed!</p>
     *
     * @param ref the reference
     * @return the node (a {@link MissingNode} if not found)
     */
    public abstract JsonNode retrieve(final JsonRef ref);

    @VisibleForTesting
    final JsonRef getCurrentRef()
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
}
