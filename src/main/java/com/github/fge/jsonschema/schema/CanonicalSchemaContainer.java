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
import com.github.fge.jsonschema.ref.JsonRef;

import java.net.URI;

/**
 * A canonical JSON Schema container
 *
 * @see AddressingMode
 */
public final class CanonicalSchemaContainer
    extends SchemaContainer
{
    CanonicalSchemaContainer(final URI uri, final JsonNode node)
    {
        super(uri, node);
    }

    @Override
    public boolean contains(final JsonRef other)
    {
        return locator.contains(other);
    }

    @Override
    public JsonNode resolve(final JsonRef ref)
    {
        return ref.getFragment().resolve(schema);
    }
}
