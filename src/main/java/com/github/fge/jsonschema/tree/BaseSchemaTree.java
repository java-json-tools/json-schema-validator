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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.exceptions.JsonReferenceException;
import com.github.fge.jsonschema.load.Dereferencing;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.util.JacksonUtils;

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
public abstract class BaseSchemaTree
    implements SchemaTree
{
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

    /**
     * Whether this schema is valid
     */
    protected final boolean valid;

    /**
     * The initial node
     */
    protected final JsonNode baseNode;

    /**
     * The current JSON Pointer into the node. Starts empty.
     */
    protected final JsonPointer pointer;

    /**
     * The current node.
     */
    protected final JsonNode node;

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
     * The current resolution context
     */
    protected final JsonRef currentRef;

    /**
     * The main constructor
     *
     * @param loadingRef the loading reference
     * @param baseNode the base node
     */
    protected BaseSchemaTree(final JsonRef loadingRef, final JsonNode baseNode,
        final Dereferencing dereferencing)
    {
        this(loadingRef, baseNode, JsonPointer.empty(), dereferencing);
    }

    /**
     * The main constructor
     *
     * @param loadingRef the loading reference
     * @param baseNode the base node
     */
    private BaseSchemaTree(final JsonRef loadingRef, final JsonNode baseNode,
        final JsonPointer pointer, final Dereferencing dereferencing)
    {
        this(loadingRef, baseNode, pointer, dereferencing, false);
    }

    protected BaseSchemaTree(final JsonRef loadingRef, final JsonNode baseNode,
        final JsonPointer pointer, final Dereferencing dereferencing,
        final boolean valid)
    {
        this.baseNode = baseNode;
        this.pointer = pointer;
        node = pointer.resolve(baseNode);
        this.dereferencing = dereferencing;
        this.loadingRef = loadingRef;


        final JsonRef ref = idFromNode(baseNode);

        startingRef = ref == null ? loadingRef : loadingRef.resolve(ref);

        currentRef = nextRef(startingRef, pointer.asElements(), baseNode);
        this.valid = valid;
    }

    @Override
    public final JsonNode getBaseNode()
    {
        return baseNode;
    }

    @Override
    public final JsonPointer getPointer()
    {
        return pointer;
    }

    @Override
    public final JsonNode getNode()
    {
        return node;
    }

    /**
     * Resolve a JSON Reference against the current resolution context
     *
     * @param other the JSON Reference to resolve
     * @return the resolved reference
     * @see JsonRef#resolve(JsonRef)
     */
    @Override
    public final JsonRef resolve(final JsonRef other)
    {
        return currentRef.resolve(other);
    }

    /**
     * Get the loading URI for that schema
     *
     * @return the loading URI as a {@link JsonRef}
     */
    @Override
    public final JsonRef getLoadingRef()
    {
        return loadingRef;
    }

    /**
     * Get the current resolution context
     *
     * @return the context as a {@link JsonRef}
     */
    @Override
    public final JsonRef getContext()
    {
        return currentRef;
    }

    @Override
    public final boolean isValid()
    {
        return valid;
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
        } catch (JsonReferenceException ignored) {
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
    public final JsonNode asJson()
    {
        final ObjectNode ret = FACTORY.objectNode();

        ret.put("loadingURI", FACTORY.textNode(loadingRef.toString()));
        ret.put("pointer", FACTORY.textNode(pointer.toString()));

        return ret;
    }

    @Override
    public final String toString()
    {
        return "loading URI: " + loadingRef
            + "; current pointer: " + pointer
            + "; resolution context: " + currentRef;
    }
}
