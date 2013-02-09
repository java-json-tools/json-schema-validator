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

package com.github.fge.jsonschema.library;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.main.JsonSchemaException;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.ref.JsonRef;
import com.google.common.base.Predicate;

public enum SchemaVersion
{
    DRAFTV4("http://json-schema.org/draft-04/schema#", DraftV4Library.get()),
    DRAFTV3("http://json-schema.org/draft-03/schema#", DraftV3Library.get()),
    ;

    private final JsonRef location;
    private final Library library;

    SchemaVersion(final String uri, final Library library)
    {
        try {
            location = JsonRef.fromString(uri);
        } catch (JsonSchemaException e) {
            throw new ExceptionInInitializerError(e);
        }
        this.library = library;
    }

    public JsonRef getLocation()
    {
        return location;
    }

    public Library getLibrary()
    {
        return library;
    }

    public Predicate<ValidationData> versionTest()
    {
        return new Predicate<ValidationData>()
        {
            @Override
            public boolean apply(final ValidationData input)
            {
                return versionMatches(input.getSchema().getBaseNode());
            }
        };
    }

    private boolean versionMatches(final JsonNode schema)
    {
        final JsonNode node = schema.path("$schema");
        if (!schema.isTextual())
            return false;
        try {
            return location.equals(JsonRef.fromString(node.textValue()));
        } catch (JsonSchemaException ignored) {
            return false;
        }
    }
}
