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

package com.github.fge.jsonschema.keyword.digest.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.keyword.digest.AbstractDigester;
import com.github.fge.jsonschema.util.NodeType;

/**
 * A "no-op" digester
 *
 * <p>This is the most simple of digesters, which will return its input as the
 * digested forms. If you choose to use it, you will therefore only have to
 * provide the list of types supported by your keyword.</p>
 */
public final class IdentityDigester
    extends AbstractDigester
{
    public IdentityDigester(final String keyword, final NodeType first,
        final NodeType... other)
    {
        super(keyword, first, other);
    }

    @Override
    public JsonNode digest(final JsonNode schema)
    {
        return schema;
    }
}
