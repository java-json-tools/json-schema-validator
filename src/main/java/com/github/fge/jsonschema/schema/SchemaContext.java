/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.jsonschema.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.ref.JsonRef;

import java.net.URI;

/**
 * A JSON Schema context
 *
 * <p>A schema context contains the JSON representation of a schema, plus its
 * location (as a {@link JsonRef}).</p>
 *
 * <p>This class embodies the ability to tell whether a JSON reference is
 * embodied within the schema itself. It is particularly important for
 * {@link AddressingMode#INLINE} addressing mode.</p>
 */
public abstract class SchemaContext
{
    protected final JsonNode schema;
    protected final JsonRef locator;

    /**
     * Return a new container based on an URI and a schema
     *
     * <p>Note that if the provided URI is not absolute, an anonymous schema is
     * returned.</p>
     *
     * @param uri the URI
     * @param node the schema
     */
    protected SchemaContext(final URI uri, final JsonNode node)
    {
        locator = JsonRef.fromURI(uri);
        schema = cleanup(node);
    }

    public abstract boolean contains(final JsonRef other);

    public abstract JsonNode resolve(final JsonRef ref);

    /**
     * Get this context's locator
     *
     * @return the locator
     */
    public final JsonRef getLocator()
    {
        return locator;
    }

    /**
     * Get this context's underlying schema
     *
     * @return the underlying {@link JsonNode}
     */
    public final JsonNode getSchema()
    {
        return schema;
    }

    @Override
    public final int hashCode()
    {
        // Yes, this works: right now there is a 1-1 relationship between URIs
        // and JsonNodes.
        return locator.hashCode();
    }

    @Override
    public final boolean equals(final Object obj)
    {
        if (obj == null)
            return false;
        if (this == obj)
            return true;

        if (!(obj instanceof SchemaContext))
            return false;

        final SchemaContext other = (SchemaContext) obj;

        // Yes, this works: right now there is a 1-1 relationship between URIs
        // and JsonNodes.
        return locator.equals(other.locator);
    }

    @Override
    public final String toString()
    {
        return "locator: " + locator;
    }

    /**
     * Strip an object instance off its {@code id} member, if any
     *
     * @param schema the victim
     * @return the copy
     */
    protected static JsonNode cleanup(final JsonNode schema)
    {
        if (!schema.has("id"))
            return schema;

        final ObjectNode ret = schema.deepCopy();

        ret.remove("id");
        return ret;
    }
}
