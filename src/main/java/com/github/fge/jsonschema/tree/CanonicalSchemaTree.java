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
import com.github.fge.jsonschema.ref.JsonFragment;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.ref.JsonRef;

public final class CanonicalSchemaTree
    extends BaseSchemaTree
{
    public CanonicalSchemaTree(final JsonNode baseNode)
    {
        this(JsonRef.emptyRef(), baseNode);
    }

    public CanonicalSchemaTree(final JsonRef loadingRef,
        final JsonNode baseNode)
    {
        this(loadingRef, baseNode, JsonPointer.empty(), false);
    }

    private CanonicalSchemaTree(final JsonRef loadingRef,
        final JsonNode baseNode, final JsonPointer pointer, final boolean valid)
    {
        super(loadingRef, baseNode, pointer, Dereferencing.CANONICAL, valid);
    }

    @Override
    public SchemaTree append(final JsonPointer pointer)
    {
        return new CanonicalSchemaTree(loadingRef, baseNode,
            this.pointer.append(pointer), valid);
    }

    @Override
    public SchemaTree setPointer(final JsonPointer pointer)
    {
        return new CanonicalSchemaTree(loadingRef, baseNode, pointer, valid);
    }

    @Override
    public boolean containsRef(final JsonRef ref)
    {
        return loadingRef.contains(ref);
    }

    @Override
    public JsonPointer matchingPointer(final JsonRef ref)
    {
        final JsonFragment fragment = ref.getFragment();

        return fragment.resolve(baseNode).isMissingNode()
            ? null : (JsonPointer) fragment;
    }

    @Override
    public SchemaTree withValidationStatus(final boolean valid)
    {
        return new CanonicalSchemaTree(loadingRef, baseNode, pointer, valid);
    }
}
