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
import com.github.fge.jsonschema.util.jackson.JacksonUtils;

public final class AdditionalItemsEquivalence
    extends KeywordEquivalence
{
    private static final KeywordEquivalence INSTANCE
        = new AdditionalItemsEquivalence();

    public static KeywordEquivalence getInstance()
    {
        return INSTANCE;
    }

    private AdditionalItemsEquivalence()
    {
        super("additionalItems", "items");
    }

    @Override
    protected JsonNode digestedNode(final JsonNode orig)
    {
        final ObjectNode ret = JacksonUtils.nodeFactory().objectNode();

        /*
         * First, let's assume that additionalItems is true or a schema
         */
        ret.put("additionalItems", true);

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
        if (orig.get(keyword).asBoolean(true))
            return ret;

        final JsonNode itemsNode = orig.path("items");

        if (itemsNode.isMissingNode() || itemsNode.isObject())
            return ret;

        /*
         * OK, "items" is there and it is an array, include its size
         */
        ret.put("items", itemsNode.size());
        return ret;
    }
}
