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
import com.github.fge.jsonschema.main.JsonSchemaException;
import com.github.fge.jsonschema.ref.JsonRef;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * An inline addressing JSON Schema container
 *
 * <p>This container will retrieve all {@code id} members in the schema
 * (including subschemas).</p>
 *
 * <p><b>NOTE:</b> if a duplicate {@code id} value is found within the schema,
 * the actual matching schema is <b>UNDEFINED</b>.</p>
 */
public final class InlineSchemaContainer
    extends SchemaContainer
{
    private final Map<JsonRef, JsonNode> schemas;

    InlineSchemaContainer(final JsonNode schema)
    {
        super(extractLocator(schema).toURI(), schema);
        final Map<JsonRef, JsonNode> map = Maps.newHashMap();

        map.put(locator, this.schema);
        fillURIMap(locator, this.schema, map);
        schemas = ImmutableMap.copyOf(map);
    }

    @Override
    public boolean contains(final JsonRef other)
    {
        return schemas.containsKey(other) || locator.contains(other);
    }

    @Override
    public JsonNode resolve(final JsonRef ref)
    {
        return schemas.containsKey(ref) ? schemas.get(ref)
            : ref.getFragment().resolve(schema);
    }

    private static JsonRef extractLocator(final JsonNode node)
    {
        try {
            return refFromNode(node.path("id"));
        } catch (JsonSchemaException ignored) {
            return JsonRef.emptyRef();
        }
    }

    private static void fillURIMap(final JsonRef baseRef, final JsonNode node,
        final Map<JsonRef, JsonNode> map)
    {
        // Do nothing if the node is not an object
        if (!node.isObject())
            return;

        JsonRef idRef, resolvedRef;

        for (final JsonNode child: node) {
            if (child.has("id"))
                try {
                    idRef = refFromNode(child.get("id"));
                    resolvedRef = baseRef.resolve(idRef);
                    map.put(resolvedRef, cleanup(child));
                } catch (JsonSchemaException ignored) {
                    // Do nothing
                }
            fillURIMap(baseRef, cleanup(child), map);
        }
    }

    private static JsonRef refFromNode(final JsonNode node)
        throws JsonSchemaException
    {
        return node.isTextual() ? JsonRef.fromString(node.textValue())
            : JsonRef.emptyRef();
    }
}
