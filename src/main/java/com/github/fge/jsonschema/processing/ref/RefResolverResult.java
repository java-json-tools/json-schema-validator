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

package com.github.fge.jsonschema.processing.ref;

import com.github.fge.jsonschema.tree.JsonSchemaTree;

public final class RefResolverResult
{
    private final JsonSchemaTree schemaTree;
    private final boolean treeChanged;
    private final boolean pointerChanged;

    public RefResolverResult(final JsonSchemaTree schemaTree,
        final boolean treeChanged, final boolean pointerChanged)
    {
        this.schemaTree = schemaTree;
        this.treeChanged = treeChanged;
        this.pointerChanged = pointerChanged;
    }

    public JsonSchemaTree getSchemaTree()
    {
        return schemaTree;
    }

    public boolean isTreeChanged()
    {
        return treeChanged;
    }

    public boolean isPointerChanged()
    {
        return pointerChanged;
    }
}
