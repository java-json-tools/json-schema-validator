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

package com.github.fge.jsonschema.load;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.InlineSchemaTree;
import com.github.fge.jsonschema.tree.SchemaTree;

/**
 * Dereferencing modes
 *
 * <p>Draft v4 defines two dereferencing modes: canonical and inline. This enum
 * defines those two modes, along with methods to generate appropriate schema
 * trees.</p>
 *
 * @see InlineSchemaTree
 * @see CanonicalSchemaTree
 */
public enum Dereferencing
{
    /**
     * Canonical dereferencing
     *
     * @see CanonicalSchemaTree
     */
    CANONICAL("canonical")
    {
        @Override
        public SchemaTree newTree(final JsonRef ref, final JsonNode node)
        {
            return new CanonicalSchemaTree(ref, node);
        }
    },
    /**
     * Inline dereferencing
     *
     * @see InlineSchemaTree
     */
    INLINE("inline")
    {
        @Override
        public SchemaTree newTree(final JsonRef ref, final JsonNode node)
        {
            return new InlineSchemaTree(ref, node);
        }
    };

    private final String name;

    /**
     * Create a new schema tree with a given loading URI and JSON Schema
     *
     * @param ref the location
     * @param node the schema
     * @return a new tree
     */
    public abstract SchemaTree newTree(final JsonRef ref, final JsonNode node);

    Dereferencing(final String name)
    {
        this.name = name;
    }

    /**
     * Create a new schema tree with an empty loading URI
     *
     * @param node the schema
     * @return a new tree
     */
    public SchemaTree newTree(final JsonNode node)
    {
        return newTree(JsonRef.emptyRef(), node);
    }

    @Override
    public String toString()
    {
        return name;
    }
}
