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
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
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
        super(loadingRef, baseNode, JsonPointer.empty());
    }

    private CanonicalSchemaTree(final CanonicalSchemaTree other,
        final JsonPointer newPointer)
    {
        super(other, newPointer);
    }

    @Override
    public SchemaTree append(final JsonPointer pointer)
    {
        final JsonPointer newPointer = this.pointer.append(pointer);
        return new CanonicalSchemaTree(this, newPointer);
    }

    @Override
    public SchemaTree setPointer(final JsonPointer pointer)
    {
        return new CanonicalSchemaTree(this, pointer);
    }

    @Override
    public boolean containsRef(final JsonRef ref)
    {
        return loadingRef.contains(ref);
    }

    @Override
    public JsonPointer matchingPointer(final JsonRef ref)
    {
        if (!ref.isLegal())
            return null;
        final JsonPointer ptr = ref.getPointer();
        return ptr.path(baseNode).isMissingNode() ? null : ptr;
    }
}
