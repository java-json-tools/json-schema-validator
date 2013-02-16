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
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.util.AsJson;

/**
 * A JSON value decorated with JSON Pointer information
 *
 * <p>This is a {@link JsonNode} with an internal path represented as a {@link
 * JsonPointer}. The current path and node are retrievable. If the current
 * pointer points to a non existent path in the document, the retrieved node is
 * a {@link MissingNode}.</p>
 *
 * @see JsonPointer
 */
public interface SimpleTree
    extends AsJson
{
    /**
     * Return the node this tree was created with
     *
     * <p>Note: in current Jackson versions, this node is unfortunately mutable,
     * so be careful...</p>
     *
     * @return the node
     */
    JsonNode getBaseNode();

    /**
     * Get the current path into the document
     *
     * @return the path as a JSON Pointer
     */
    JsonPointer getPointer();

    /**
     * Get the node at the current path
     *
     * @return the matching node (a {@link MissingNode} if there is no matching
     * node at that pointer)
     */
    JsonNode getNode();
}
