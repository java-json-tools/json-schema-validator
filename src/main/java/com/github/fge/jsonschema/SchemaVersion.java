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

package com.github.fge.jsonschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.util.JsonLoader;

import java.io.IOException;
import java.net.URI;

public enum SchemaVersion
{
    DRAFTV4("http://json-schema.org/draft-04/schema#", "/draftv4/schema"),
    DRAFTV3("http://json-schema.org/draft-03/schema#", "/draftv3/schema"),
    ;

    private final URI location;
    private final JsonNode schema;

    SchemaVersion(final String uri, final String resource)
    {
        try {
            location = URI.create(uri);
            schema = JsonLoader.fromResource(resource);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public URI getLocation()
    {
        return location;
    }

    public JsonNode getSchema()
    {
        return schema.deepCopy();
    }
}
