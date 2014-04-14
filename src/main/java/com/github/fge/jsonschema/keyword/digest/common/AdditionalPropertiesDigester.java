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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.keyword.digest.AbstractDigester;
import com.github.fge.jsonschema.keyword.digest.Digester;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

/**
 * Digester for {@code additionalProperties}
 *
 * <p>The digested form will contain the list of members from {@code properties}
 * and {@code patternProperties}, unless this keyword is {@code true} or a
 * schema, in which case additional properties are always allowed.</p>
 */
public final class AdditionalPropertiesDigester
    extends AbstractDigester
{
    private static final Digester INSTANCE = new AdditionalPropertiesDigester();

    public static Digester getInstance()
    {
        return INSTANCE;
    }

    private AdditionalPropertiesDigester()
    {
        super("additionalProperties", NodeType.OBJECT);
    }

    @Override
    public JsonNode digest(final JsonNode schema)
    {
        final ObjectNode ret = FACTORY.objectNode();
        final ArrayNode properties = FACTORY.arrayNode();
        final ArrayNode patternProperties = FACTORY.arrayNode();

        /*
         * Start by presuming that additional properties are allowed. This will
         * not be the case if and only if it has boolean value false.
         */
        ret.put(keyword, true);
        ret.put("properties", properties);
        ret.put("patternProperties", patternProperties);

        if (schema.get(keyword).asBoolean(true))
            return ret;

        /*
         * OK, it is false... Therefore collect the list of defined property
         * names and regexes. Put them in order, we don't want to generate two
         * different digests for properties p, q and q, p.
         */
        ret.put(keyword, false);

        List<String> list;

        list = Lists.newArrayList(schema.path("properties").fieldNames());
        Collections.sort(list);
        for (final String s: list)
            properties.add(s);

        list = Lists.newArrayList(schema.path("patternProperties")
            .fieldNames());
        Collections.sort(list);
        for (final String s: list)
            patternProperties.add(s);

        return ret;
    }
}
