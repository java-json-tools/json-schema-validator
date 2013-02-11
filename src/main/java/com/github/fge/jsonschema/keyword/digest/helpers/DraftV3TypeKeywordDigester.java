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

package com.github.fge.jsonschema.keyword.digest.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.keyword.digest.AbstractDigester;
import com.github.fge.jsonschema.util.NodeType;

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
