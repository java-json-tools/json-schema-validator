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

package org.eel.kitchen.jsonschema.ref;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;

import java.net.URI;

/**
 * A JSON Schema container
 *
 * <p>This embodies the schema itself, as a {@link JsonNode}, and its locator,
 * as a {@link JsonRef}.</p>
 *
 * <p>Note that the schema passed as an argument will be stripped off its
 * {@code id} field, if it has one.</p>
 */
public final class SchemaContainer
{
    private final JsonNode schema;
    private final JsonRef locator;

    /**
     * Return a new container based on a schema
     *
     * <p>Note that if the {@code id} node exists and is an URI,
     * but not absolute, an anonymous schema will be returned.
     * </p>
     *
     * @param schema the schema
     */
    public SchemaContainer(final JsonNode schema)
    {
        JsonRef ref;

        try {
            ref = JsonRef.fromNode(schema.path("id"));
        } catch (JsonSchemaException ignored) {
            locator = JsonRef.emptyRef();
            this.schema = schema;
            return;
        }

        if (!ref.isAbsolute())
            ref = JsonRef.emptyRef();

        locator = ref;
        this.schema = cleanup(schema);
    }

    /**
     * Return a new container based on an URI and a schema
     *
     * <p>Note that if the provided URI is not absolute, an
     * anonymous schema is returned.</p>
     *
     * @param uri the URI
     * @param node the schema
     */
    SchemaContainer(final URI uri, final JsonNode node)
    {
        locator = JsonRef.fromURI(uri);
        schema = cleanup(node);
    }

    /**
     * Get this container's locator
     *
     * @return the locator
     */
    public JsonRef getLocator()
    {
        return locator;
    }

    /**
     * Get this container's underlying schema
     *
     * @return the underlying {@link JsonNode}
     */
    public JsonNode getSchema()
    {
        return schema;
    }

    @Override
    public int hashCode()
    {
        // Yes, this works: right now there is a 1-1 relationship between URIs
        // and JsonNodes.
        return locator.hashCode();
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj == null)
            return false;
        if (this == obj)
            return true;

        if (getClass() != obj.getClass())
            return false;

        final SchemaContainer other = (SchemaContainer) obj;

        // Yes, this works: right now there is a 1-1 relationship between URIs
        // and JsonNodes.
        return locator.equals(other.locator);
    }

    @Override
    public String toString()
    {
        return "locator: " + locator;
    }

    /**
     * Strip an object instance off its {@code id} member, if any
     *
     * @param schema the victim
     * @return the copy
     */
    private static JsonNode cleanup(final JsonNode schema)
    {
        if (!schema.has("id"))
            return schema;

        final ObjectNode ret = schema.deepCopy();

        ret.remove("id");
        return ret;
    }
}
