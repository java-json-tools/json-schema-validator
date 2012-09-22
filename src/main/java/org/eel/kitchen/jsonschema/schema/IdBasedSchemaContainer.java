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

package org.eel.kitchen.jsonschema.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.ref.JsonRef;

import java.util.Map;

/**
 * A JSON Schema container
 *
 * <b>TODO: javadoc</b>
 */
public final class IdBasedSchemaContainer
{
    private final JsonRef locator;
    private final JsonNode schema;
    private final Map<JsonRef, JsonNode> schemas;

    IdBasedSchemaContainer(final JsonNode schema)
    {
        final ImmutableMap.Builder<JsonRef, JsonNode> builder
            = new ImmutableMap.Builder<JsonRef, JsonNode>();

        locator = extractLocator(schema);
        this.schema = cleanup(schema);
        builder.put(locator, this.schema);
        fillURIMap(locator, this.schema, builder);
        schemas = builder.build();
    }

    public boolean contains(final JsonRef other)
    {
        return schemas.containsKey(other) || locator.contains(other);
    }

    public JsonNode resolve(final JsonRef ref)
    {
        return schemas.containsKey(ref) ? schemas.get(ref)
            : ref.getFragment().resolve(schema);
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

        final IdBasedSchemaContainer other = (IdBasedSchemaContainer) obj;

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

    private static JsonRef extractLocator(final JsonNode node)
    {
        if (!node.has("id"))
            return JsonRef.emptyRef();

        try {
            return JsonRef.fromNode(node.get("id"));
        } catch (JsonSchemaException ignored) {
            return JsonRef.emptyRef();
        }
    }

    private static void fillURIMap(final JsonRef baseRef, final JsonNode node,
        final ImmutableMap.Builder<JsonRef, JsonNode> builder)
    {
        // Do nothing if the node is not an object
        if (!node.isObject())
            return;

        JsonRef idRef, resolvedRef;

        for (final JsonNode child: node) {
            if (child.has("id")) {
                try {
                    idRef = JsonRef.fromNode(child.get("id"));
                    resolvedRef = baseRef.resolve(idRef);
                    builder.put(resolvedRef, cleanup(child));
                } catch (JsonSchemaException ignored) {
                    // Do nothing
                }
            }
            fillURIMap(baseRef, cleanup(child), builder);
        }
    }
}
