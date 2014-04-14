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

package com.github.fge.jsonschema.keyword.digest.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.keyword.digest.AbstractDigester;

import java.util.EnumSet;

/**
 * Digester for draft v3's {@code type} and {@code disallow}
 *
 * <p>These keywords are quite complex, but fortunately they share the same
 * fundamental structure. Simple types and schema dependencies are stored
 * separately.</p>
 */
public final class DraftV3TypeKeywordDigester
    extends AbstractDigester
{
    private static final String ANY = "any";

    public DraftV3TypeKeywordDigester(final String keyword)
    {
        super(keyword, NodeType.ARRAY, NodeType.values());
    }

    @Override
    public JsonNode digest(final JsonNode schema)
    {
        final ObjectNode ret = FACTORY.objectNode();
        final ArrayNode simpleTypes = FACTORY.arrayNode();
        ret.put(keyword, simpleTypes);
        final ArrayNode schemas = FACTORY.arrayNode();
        ret.put("schemas", schemas);

        final JsonNode node = schema.get(keyword);

        final EnumSet<NodeType> set = EnumSet.noneOf(NodeType.class);

        if (node.isTextual()) // Single type
            putType(set, node.textValue());
        else { // More than one type, and possibly schemas
            final int size = node.size();
            JsonNode element;
            for (int index = 0; index < size; index++) {
                element = node.get(index);
                if (element.isTextual())
                    putType(set, element.textValue());
                else
                    schemas.add(index);
            }
        }

        /*
         * If all types are there, no need to collect schemas
         */
        if (EnumSet.complementOf(set).isEmpty())
            schemas.removeAll();

        /*
         * Note that as this is an enumset, order is guaranteed
         */
        for (final NodeType type: set)
            simpleTypes.add(type.toString());

        return ret;
    }

    private static void putType(final EnumSet<NodeType> types, final String s)
    {
        if (ANY.equals(s)) {
            types.addAll(EnumSet.allOf(NodeType.class));
            return;
        }

        final NodeType type = NodeType.fromName(s);
        types.add(type);

        if (type == NodeType.NUMBER)
            types.add(NodeType.INTEGER);
    }
}
