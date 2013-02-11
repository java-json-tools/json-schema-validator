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

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.tree.InlineSchemaTree2;
import com.github.fge.jsonschema.tree.SchemaTree;

public enum Dereferencing
{
    CANONICAL("canonical")
    {
        @Override
        public SchemaTree newTree2(final JsonRef ref, final JsonNode node)
        {
            return new InlineSchemaTree2(ref, node);
        }
    },
    INLINE("inline")
    {
        @Override
        public SchemaTree newTree2(final JsonRef ref, final JsonNode node)
        {
            return new InlineSchemaTree2(ref, node);
        }
    };

    private final String name;

    public abstract SchemaTree newTree2(final JsonRef ref,
        final JsonNode node);

    Dereferencing(final String name)
    {
        this.name = name;
    }

    public SchemaTree newTree2(final JsonNode node)
    {
        return newTree2(JsonRef.emptyRef(), node);
    }

    @Override
    public String toString()
    {
        return name;
    }
}
