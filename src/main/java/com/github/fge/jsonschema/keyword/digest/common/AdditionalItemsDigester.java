/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.keyword.digest.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.keyword.digest.AbstractDigester;
import com.github.fge.jsonschema.keyword.digest.Digester;

/**
 * Digester for {@code additionalItems}
 *
 * <p>The digested form is very simple: additional items are always allowed
 * unless the keword is {@code false} <i>and</i> {@code items} is an array. In
 * this last case, the size of the {@code items} array is stored.</p>
 */
public final class AdditionalItemsDigester
    extends AbstractDigester
{
    private static final Digester INSTANCE
        = new AdditionalItemsDigester();

    public static Digester getInstance()
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
