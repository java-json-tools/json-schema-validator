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
import com.github.fge.jsonschema.keyword.digest.AbstractKeywordDigester;
import com.github.fge.jsonschema.keyword.digest.KeywordDigester;
import com.github.fge.jsonschema.util.NodeType;

public final class AdditionalItemsDigester
    extends AbstractKeywordDigester
{
    private static final KeywordDigester INSTANCE
        = new AdditionalItemsDigester();

    public static KeywordDigester getInstance()
    {
        return INSTANCE;
    }

    private AdditionalItemsDigester()
    {
        super("additionalItems", NodeType.ARRAY);
    }

    @Override
    public JsonNode digest(final JsonNode schema)
    {
        final ObjectNode ret = FACTORY.objectNode();

        /*
         * First, let's assume that additionalItems is true or a schema
         */
        ret.put(keyword, true);
        ret.put("itemsSize", 0);

        /*
         * If it is false:
         *
         * - if "items" is an object, nevermind;
         * - but if it is an array, set it to false and include the array size.
         *
         * We use .asBoolean() here since it does what we want: we know the
         * syntax is correct, so this will return false if and only if
         * additionalItems itself is boolean false. We return true as the
         * default value.
         */
        if (schema.get(keyword).asBoolean(true))
            return ret;

        final JsonNode itemsNode = schema.path("items");

        if (!itemsNode.isArray())
            return ret;

        /*
         * OK, "items" is there and it is an array, include its size
         */
        ret.put(keyword, false);
        ret.put("itemsSize", itemsNode.size());
        return ret;
    }
}
