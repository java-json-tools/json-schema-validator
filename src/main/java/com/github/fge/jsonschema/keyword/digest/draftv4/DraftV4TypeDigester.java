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

import java.util.EnumSet;

/**
 * Digester for {@code type} (draft v4)
 *
 * <p>This will store a set of allowed types. It will, for instance, produce the
 * same digested form of these two forms:</p>
 *
 * <ul>
 *     <li>{@code {"type": "string" } }</li>
 *     <li>{@code { "type": [ "string" ] } }</li>
 * </ul>
 */
public final class DraftV4TypeDigester
    extends AbstractDigester
{
    private static final Digester INSTANCE = new DraftV4TypeDigester();

    public static Digester getInstance()
    {
        return INSTANCE;
    }

    private DraftV4TypeDigester()
    {
        super("type", NodeType.ARRAY, NodeType.values());
    }

    @Override
    public JsonNode digest(final JsonNode schema)
    {
        final ObjectNode ret = FACTORY.objectNode();
        final ArrayNode allowedTypes = FACTORY.arrayNode();
        ret.put(keyword, allowedTypes);

        final JsonNode node = schema.get(keyword);

        final EnumSet<NodeType> typeSet = EnumSet.noneOf(NodeType.class);

        if (node.isTextual()) // Single type
            typeSet.add(NodeType.fromName(node.textValue()));
        else // More than one type
            for (final JsonNode element: node)
                typeSet.add(NodeType.fromName(element.textValue()));

        if (typeSet.contains(NodeType.NUMBER))
            typeSet.add(NodeType.INTEGER);

        /*
         * Note that as this is an enumset, order is guaranteed
         */
        for (final NodeType type: typeSet)
            allowedTypes.add(type.toString());

        return ret;
    }
}
