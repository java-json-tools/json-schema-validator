/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
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

package org.eel.kitchen.jsonschema.util;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration for the different types of JSON instances which can be
 * encountered.
 */

public enum NodeType
{
    /**
     * Array nodes
     */
    ARRAY("array"),
    /**
     * Boolean nodes
     */
    BOOLEAN("boolean"),
    /**
     * Integer nodes
     */
    INTEGER("integer"),
    /**
     * Number nodes (ie, decimal numbers)
     */
    NUMBER("number"),
    /**
     * Null nodes
     */
    NULL("null"),
    /**
     * Object nodes
     */
    OBJECT("object"),
    /**
     * String nodes
     */
    STRING("string");

    /**
     * The name for this type, as encountered in a JSON schema
     */
    private final String name;

    private static final Map<String, NodeType> nameMap
        = new HashMap<String, NodeType>();

    /**
     * Mapping of {@link JsonToken} back to node types (used in
     * {@link #getNodeType(JsonNode)})
     */
    private static final Map<JsonToken, NodeType> reverseMap
        = new EnumMap<JsonToken, NodeType>(JsonToken.class);

    static {
        reverseMap.put(JsonToken.START_ARRAY, ARRAY);
        reverseMap.put(JsonToken.VALUE_TRUE, BOOLEAN);
        reverseMap.put(JsonToken.VALUE_FALSE, BOOLEAN);
        reverseMap.put(JsonToken.VALUE_NUMBER_INT, INTEGER);
        reverseMap.put(JsonToken.VALUE_NUMBER_FLOAT, NUMBER);
        reverseMap.put(JsonToken.VALUE_NULL, NULL);
        reverseMap.put(JsonToken.START_OBJECT, OBJECT);
        reverseMap.put(JsonToken.VALUE_STRING, STRING);

        for (final NodeType type: NodeType.values())
            nameMap.put(type.name, type);
    }

    NodeType(final String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }

    /**
     * Given a type name, return the corresponding node type
     *
     * @param name the type name
     * @return the node type, or null if not found
     */
    public static NodeType fromName(final String name)
    {
        return nameMap.get(name);
    }

    /**
     * Given a {@link JsonNode} as an argument, return its type. The argument
     * MUST NOT BE NULL, and MUST NOT be a {@link MissingNode}
     *
     * @param node the node to determine the type of
     * @return the type for this node
     */
    public static NodeType getNodeType(final JsonNode node)
    {
        final JsonToken token = node.asToken();
        final NodeType ret = reverseMap.get(token);

        if (ret == null)
            throw new IllegalArgumentException("unhandled token type " + token);

        return ret;
    }
}
