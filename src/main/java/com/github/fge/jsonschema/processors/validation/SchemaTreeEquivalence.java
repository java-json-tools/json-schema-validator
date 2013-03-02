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

package com.github.fge.jsonschema.processors.validation;

import com.github.fge.jsonschema.tree.SchemaTree;
import com.google.common.base.Equivalence;

/**
 * Schema tree equivalence
 *
 * <p>This is used by {@link ValidationChain} to cache results of the JSON
 * Reference resolution -> syntax validation chain.</p>
 *
 * <p>Two schema trees are considered equivant if their loading URI, current
 * URI context, base node and pointers are equivalent.</p>
 */
// TODO REMOVE
public final class SchemaTreeEquivalence
    extends Equivalence<SchemaTree>
{
    private static final Equivalence<SchemaTree> INSTANCE
        = new SchemaTreeEquivalence();

    public static Equivalence<SchemaTree> getInstance()
    {
        return INSTANCE;
    }

    private SchemaTreeEquivalence()
    {
    }

    @Override
    protected boolean doEquivalent(final SchemaTree a, final SchemaTree b)
    {
        return a.getLoadingRef().equals(b.getLoadingRef())
            && a.getContext().equals(b.getContext())
            && a.getPointer().equals(b.getPointer())
            && a.getBaseNode().equals(b.getBaseNode());
    }

    @Override
    protected int doHash(final SchemaTree t)
    {
        int ret = t.getLoadingRef().hashCode();
        ret = 31 * ret + t.getContext().hashCode();
        ret = 31 * ret + t.getPointer().hashCode();
        ret = 31 * ret + t.getBaseNode().hashCode();
        return ret;
    }
}
