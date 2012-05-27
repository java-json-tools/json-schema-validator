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
import org.eel.kitchen.jsonschema.main.JsonSchemaException;

public final class SchemaContainer
{
    private final JsonNode schema;
    private JsonRef locator;

    public SchemaContainer(final JsonNode schema)
        throws JsonSchemaException
    {
        this.schema = schema;
        locator = JsonRef.fromNode(schema, "id");
        if (!locator.isAbsolute() && !locator.isEmpty())
            throw new JsonSchemaException("a parent schema's id must be "
                + "absolute");
    }

    public boolean contains(final JsonRef ref)
    {
        final JsonRef tmp = locator.resolve(ref);

        return locator.getLocator().equals(tmp.getLocator());
    }
}
