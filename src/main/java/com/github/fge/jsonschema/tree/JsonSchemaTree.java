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

import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.ref.JsonRef;

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
 * @see CanonicalSchemaTree
 * @see InlineSchemaTree
 */
public interface JsonSchemaTree
    extends JsonTree
{
    void setPointer(final JsonPointer pointer);

    /**
     * Resolve a JSON Reference against the current resolution context
     *
     * @param other the JSON Reference to resolve
     * @return the resolved reference
     * @see JsonRef#resolve(JsonRef)
     */
    JsonRef resolve(final JsonRef other);

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
    boolean containsRef(final JsonRef ref);

    /**
     * Return a matching pointer in this tree for a fully resolved reference
     *
     * <p>This must be called <b>only</b> when {@link #containsRef(JsonRef)}
     * returns {@code true}.</p>
     *
     * @param ref the reference
     * @return the matching pointer, or {@code null} if not found
     */
    JsonPointer matchingPointer(final JsonRef ref);

    /**
     * Get the loading URI for that schema
     *
     * @return the loading URI as a {@link JsonRef}
     */
    JsonRef getLoadingRef();

    /**
     * Get the current resolution context
     *
     * @return the context as a {@link JsonRef}
     */
    JsonRef getCurrentRef();

    /**
     * Return a copy of this tree at its current state but with an empty stack
     *
     * @return the copy
     */
    JsonSchemaTree copy();
}
