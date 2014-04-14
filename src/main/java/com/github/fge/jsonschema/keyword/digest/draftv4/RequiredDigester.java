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

package com.github.fge.jsonschema.keyword.digest.draftv4;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.keyword.digest.AbstractDigester;
import com.github.fge.jsonschema.keyword.digest.Digester;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Digester for {@code required}
 *
 * <p>Its role is simply enough to ensure that, for instance, {@code
 * ["a", "b"]} and {@code ["b", "a"]} produce the same output.</p>
 */
public final class RequiredDigester
    extends AbstractDigester
{
    private static final Digester INSTANCE = new RequiredDigester();

    public static Digester getInstance()
    {
        return INSTANCE;
    }

    private RequiredDigester()
    {
        super("required", NodeType.OBJECT);
    }

    @Override
    public JsonNode digest(final JsonNode schema)
    {
        final ObjectNode ret = FACTORY.objectNode();
        final ArrayNode required = FACTORY.arrayNode();
        ret.put(keyword, required);

        final List<JsonNode> list = Lists.newArrayList(schema.get(keyword));

        Collections.sort(list, new Comparator<JsonNode>()
        {
            @Override
            public int compare(final JsonNode o1, final JsonNode o2)
            {
                return o1.textValue().compareTo(o2.textValue());
            }
        });

        required.addAll(list);
        return ret;
    }
}
