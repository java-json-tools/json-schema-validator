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

package com.github.fge.jsonschema.util.equivalence;

import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.google.common.base.Equivalence;

public final class SyntaxCheckingEquivalence
    extends Equivalence<JsonSchemaTree>
{
    private static final Equivalence<JsonSchemaTree> INSTANCE
        = new SyntaxCheckingEquivalence();

    private SyntaxCheckingEquivalence()
    {
    }

    public static Equivalence<JsonSchemaTree> getInstance()
    {
        return INSTANCE;
    }

    @Override
    protected boolean doEquivalent(final JsonSchemaTree a,
        final JsonSchemaTree b)
    {
        return a.getCurrentPointer().equals(b.getCurrentPointer())
            && a.getBaseNode().equals(b.getBaseNode());
    }

    @Override
    protected int doHash(final JsonSchemaTree t)
    {
        return 31 * t.getCurrentPointer().hashCode()
            + t.getBaseNode().hashCode();
    }
}
