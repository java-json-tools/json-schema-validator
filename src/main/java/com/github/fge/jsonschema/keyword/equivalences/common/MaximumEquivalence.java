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

package com.github.fge.jsonschema.keyword.equivalences.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.keyword.equivalences.KeywordEquivalence;

public final class MaximumEquivalence
    extends KeywordEquivalence
{
    private static final KeywordEquivalence INSTANCE = new MaximumEquivalence();

    public static KeywordEquivalence getInstance()
    {
        return INSTANCE;
    }

    private MaximumEquivalence()
    {
        super("maximum", "exclusiveMaximum");
    }

    @Override
    protected JsonNode digestedNode(final JsonNode orig)
    {
        final ObjectNode ret = FACTORY.objectNode();
        ret.put(keyword, FACTORY.numberNode(orig.get(keyword).decimalValue()));

        if (orig.path("exclusiveMaximum").asBoolean(false))
            ret.put("exclusiveMaximum", true);

        return ret;
    }
}
