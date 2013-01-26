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
import com.github.fge.jsonschema.ref.JsonFragment;
import com.github.fge.jsonschema.ref.JsonRef;

/**
 * A {@link JsonSchemaTree} using canonical dereferencing
 *
 * <p>When using canonical dereferencing, a (fully resolved) JSON Reference is
 * contained within the tree if and only if:</p>
 *
 * <ul>
 *     <li>this reference without a fragment part is the same as this tree's
 *     loading reference without a fragment part;</li>
 *     <li>this reference's fragment part, if any, is a JSON Pointer.</li>
 * </ul>
 *
 * @see JsonSchemaTree
 * @see JsonRef#contains(JsonRef)
 * @see JsonFragment#isPointer()
 */
public final class CanonicalSchemaTree
    extends JsonSchemaTree
{
    public CanonicalSchemaTree(final JsonRef loadingRef,
        final JsonNode baseNode)
    {
        super(loadingRef, baseNode);
    }

    public CanonicalSchemaTree(final JsonNode baseNode)
    {
        super(baseNode);
    }

    @Override
    public boolean contains(final JsonRef ref)
    {
        return loadingRef.contains(ref);
    }

    @Override
    public JsonNode retrieve(final JsonRef ref)
    {
        return ref.getFragment().resolve(baseNode);
    }
}
