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

package com.github.fge.jsonschema.processing.ref;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.ref.JsonRef;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.net.URI;
import java.util.Map;

/**
 * A schema bundle
 *
 * <p>You can use this class to register a set of schemas and pass it to your
 * schema factory via a builder.</p>
 *
 * <p>Note that URIs must be valid, absolute JSON references, which means that
 * not only the URI itself must be absolute, but also have no fragment or an
 * empty fragment.</p>
 *
 * <p>Note also that the validity of the schemas is <b>not</b> checked at this
 * stage.</p>
 */
public final class SchemaBundle
{
    /**
     * Map of schemas
     */
    private final Map<URI, JsonNode> schemas = Maps.newHashMap();

    /**
     * Add a schema to the bundle
     *
     * @param uri the URI of this schema
     * @param schema the schema as a JSON document
     * @throws IllegalArgumentException the URI is not an absolute JSON
     * Reference
     */
    public void addSchema(final URI uri, final JsonNode schema)
    {
        final JsonRef ref = JsonRef.fromURI(uri);
        Preconditions.checkArgument(ref.isAbsolute(),
            "provided URI " + uri + " is not an absolute schema URI");

        schemas.put(ref.getLocator(), schema);
    }

    /**
     * Add a schema to the bundle
     *
     * @param uri the URI of this schema as a string
     * @param schema the schema as a JSON document
     * @throws IllegalArgumentException {@code uri} is not a URI, or the
     * generated URI is not an absolute JSON Reference
     */
    public void addSchema(final String uri, final JsonNode schema)
    {
        addSchema(URI.create(uri), schema);
    }

    public Map<URI, JsonNode> getSchemas()
    {
        return ImmutableMap.copyOf(schemas);
    }
}
