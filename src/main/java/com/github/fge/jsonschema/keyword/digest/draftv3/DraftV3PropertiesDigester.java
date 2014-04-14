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

package com.github.fge.jsonschema.keyword.digest.draftv3;

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
 * Digester for draft v3's {@code properties} keyword
 *
 * <p>This stores the same information as draft v4's {@code required}.</p>
 */
public final class DraftV3PropertiesDigester
    extends AbstractDigester
{
    private static final Digester INSTANCE = new DraftV3PropertiesDigester();

    public static Digester getInstance()
    {
        return INSTANCE;
    }

    private DraftV3PropertiesDigester()
    {
        super("properties", NodeType.OBJECT);
    }

    @Override
    public JsonNode digest(final JsonNode schema)
    {
        // TODO: return an array directly (same for "required" in v4)
        final ObjectNode ret = FACTORY.objectNode();
        final ArrayNode required = FACTORY.arrayNode();
        ret.put("required", required);

        final JsonNode node = schema.get(keyword);
        final List<String> list = Lists.newArrayList(node.fieldNames());

        Collections.sort(list);

        for (final String field: list)
            if (node.get(field).path("required").asBoolean(false))
                required.add(field);

        return ret;
    }
}
