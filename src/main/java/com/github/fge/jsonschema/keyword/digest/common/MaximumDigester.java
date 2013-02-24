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

package com.github.fge.jsonschema.keyword.digest.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.keyword.digest.Digester;
import com.github.fge.jsonschema.keyword.digest.helpers.NumericDigester;

/**
 * Digester for {@code maximum}
 *
 * <p>This uses {@link NumericDigester} as a base, and also stores information
 * about the presence (or not) of {@code exclusiveMaximum}.</p>
 */
public final class MaximumDigester
    extends NumericDigester
{
    private static final Digester INSTANCE = new MaximumDigester();

    public static Digester getInstance()
    {
        return INSTANCE;
    }

    private MaximumDigester()
    {
        super("maximum");
    }
    @Override
    public JsonNode digest(final JsonNode schema)
    {
        final ObjectNode ret = digestedNumberNode(schema);
        ret.put("exclusive", schema.path("exclusiveMaximum").asBoolean(false));
        return ret;
    }
}
