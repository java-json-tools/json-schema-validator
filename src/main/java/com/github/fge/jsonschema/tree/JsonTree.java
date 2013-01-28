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
import com.github.fge.jsonschema.util.AsJson;

/**
 * A JSON value decorated with JSON Pointer information
 *
 * <p>This is a {@link JsonNode} with an internal, modifiable path represented
 * as a {@link JsonPointer}. The current path and node are retrievable.</p>
 *
 * <p>There are two operations to modify the current path:</p>
 *
 * <ul>
 *     <li>{@link #append(JsonPointer)} will append a pointer to the current
 *     path (like a relative {@code cd});</li>
 *     <li>{@link #pop()} will get back to the previous path.</li>
 * </ul>
 *
 * <p>An initialized tree always starts at the root of the wrapped JSON
 * document. If the current pointer points to a non existent path in the
 * document, the retrieved node is a {@link MissingNode}.</p>
 *
 * <p>Finally, remember that <b>JSON Pointers are always absolute,</b> which
 * means that <b>appending {@code ..} over {@code /a/b} will not lead to {@code
 * /a}, but {@code /a/b/..}</b>. {@code ..} is a perfectly valid member name for
 * a JSON Object!</p>
 *
 * @see JsonPointer
 */
public interface JsonTree
    extends AsJson
{
    /**
     * Append a JSON Pointer to the current path
     *
     * @param ptr the pointer to append
     * @see JsonPointer#append(JsonPointer)
     */
    void append(final JsonPointer ptr);

    /**
     * Reverts the last append
     *
     * <p>Note: this operation will fail badly if you haven't appended anything,
     * so use with care!</p>
     */
    void pop();

    /**
     * Get the current path into the document
     *
     * @return the path as a JSON Pointer
     */
    JsonPointer getCurrentPointer();

    /**
     * Get the node at the current path
     *
     * @return the matching node (a {@link MissingNode} if there is no matching
     * node at that pointer)
     */
    JsonNode getCurrentNode();
}

