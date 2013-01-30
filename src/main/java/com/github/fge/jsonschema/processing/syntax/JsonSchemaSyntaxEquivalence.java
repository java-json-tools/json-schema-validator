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

package com.github.fge.jsonschema.processing.syntax;

import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.google.common.base.Equivalence;

/**
 * Equivalence class specifically defined for syntax checking
 *
 * <p>By default, {@link JsonSchemaTree}'s equality is based on the loading
 * JSON Reference and schema. But for syntax checking we need to compare the
 * schema with the current location in it, so that we can accurately report
 * non visited paths and look up these as keys.</p>
 */
public final class JsonSchemaSyntaxEquivalence
    extends Equivalence<JsonSchemaTree>
{
    /**
     * Returns {@code true} if {@code a} and {@code b} are considered equivalent.
     * <p>Called by {@link #equivalent}. {@code a} and {@code b} are not the same
     * object and are not nulls.
     *
     * @since 10.0 (previously, subclasses would override equivalent())
     */
    @Override
    protected boolean doEquivalent(final JsonSchemaTree a,
        final JsonSchemaTree b)
    {
        return a.getCurrentRef().equals(b.getCurrentRef())
            && a.getBaseNode().equals(b.getBaseNode());
    }

    /**
     * Returns a hash code for non-null object {@code t}.
     * <p>Called by {@link #hash}.
     *
     * @since 10.0 (previously, subclasses would override hash())
     */
    @Override
    protected int doHash(final JsonSchemaTree t)
    {
        return 31 * t.getCurrentRef().hashCode() + t.getBaseNode().hashCode();
    }
}
