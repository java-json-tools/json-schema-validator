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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.ref.JsonRef;

import java.net.URI;
import java.util.Map;

/**
 * A schema bundle
 *
 * <p>You can use this class to register a set of schemas and pass it to your
 * schema factory. The first schema you will put into a bundle will be
 * considered to be the main schema, so insertion order is significant.</p>
 *
 * <p>A bundle associates URIs with schemas using the following rules:</p>
 *
 * <ul>
 *     <li>the URI must be absolute, and have no, or an empty, fragment part;
 *     </li>
 *     <li>if you submit a schema without a URI, it is expected that this schema
 *     has an {@code id} member, and that the value of this member is a URI
 *     following the same rules as above.</li>
 * </ul>
 *
 * <p>The set of schemas in a bundle will be injected into the schema registry
 * associated to the factory.</p>
 *
 * @see JsonSchemaFactory
 * @see SchemaRegistry
 */
public final class SchemaBundle
{
    private final JsonNode rootSchema;
    private final Map<URI, JsonNode> schemas = Maps.newHashMap();

    private SchemaBundle(final URI uri, final JsonNode schema)
    {
        rootSchema = schema;
        schemas.put(uri, schema);
    }

    public static SchemaBundle withRootSchema(final URI uri,
        final JsonNode schema)
    {
        final JsonRef ref = JsonRef.fromURI(uri);
        if (!ref.isAbsolute())
            throw new IllegalArgumentException("Provided URI " + uri + " is not"
                + " an absolute schema URI");

        return new SchemaBundle(ref.getLocator(), schema);
    }

    public static SchemaBundle withRootSchema(final String uriAsString,
        final JsonNode schema)
    {
        return withRootSchema(URI.create(uriAsString), schema);
    }

    public static SchemaBundle withRootSchema(final JsonNode schema)
    {
        if (!schema.has("id"))
            throw new IllegalArgumentException("schema has no \"id\" member");

        final JsonRef ref;
        try {
            ref = JsonRef.fromNode(schema.get("id"));
            if (!ref.isAbsolute())
                throw new IllegalArgumentException("schema's id is not a valid"
                    + " schema locator");
        } catch (JsonSchemaException ignored) {
            throw new IllegalArgumentException("schema's id is not a valid"
                + " schema locator");
        }

        return new SchemaBundle(ref.getLocator(), schema);
    }

    public JsonNode getRootSchema()
    {
        return rootSchema;
    }

    public Map<URI, JsonNode> getSchemas()
    {
        return ImmutableMap.copyOf(schemas);
    }
}
