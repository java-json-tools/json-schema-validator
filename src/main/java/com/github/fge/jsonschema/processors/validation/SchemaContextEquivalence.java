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

import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.core.util.equivalence.SchemaTreeEquivalence;
import com.github.fge.jsonschema.processors.data.SchemaContext;
import com.google.common.base.Equivalence;

/**
 * Equivalence for schema contexts
 *
 * <p>This is used by {@link ValidationChain} and {@link ValidationProcessor} to
 * cache computation results. Two schema contexts are considered equivalent if:
 * </p>
 *
 * <ul>
 *     <li>schema trees are considered equivalent,</li>
 *     <li>and the type of the instance is the same.</li>
 * </ul>
 *
 * @see SchemaTreeEquivalence
 */
public final class SchemaContextEquivalence
    extends Equivalence<SchemaContext>
{
    private static final Equivalence<SchemaContext> INSTANCE
        = new SchemaContextEquivalence();

    private static final Equivalence<SchemaTree> TREE_EQUIVALENCE
        = SchemaTreeEquivalence.getInstance();

    public static Equivalence<SchemaContext> getInstance()
    {
        return INSTANCE;
    }

    @Override
    protected boolean doEquivalent(final SchemaContext a, final SchemaContext b)
    {
        return TREE_EQUIVALENCE.equivalent(a.getSchema(), b.getSchema())
            && a.getInstanceType() == b.getInstanceType();
    }

    @Override
    protected int doHash(final SchemaContext t)
    {
        return 31 * TREE_EQUIVALENCE.hash(t.getSchema())
            + t.getInstanceType().hashCode();
    }
}
