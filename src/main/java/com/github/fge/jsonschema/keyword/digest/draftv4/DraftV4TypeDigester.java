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

package com.github.fge.jsonschema.keyword.digest.draftv4;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.keyword.digest.AbstractDigester;
import com.github.fge.jsonschema.keyword.digest.Digester;
import com.github.fge.jsonschema.util.NodeType;

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
