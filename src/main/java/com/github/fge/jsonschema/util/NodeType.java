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

package com.github.fge.jsonschema.util;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.util.EnumMap;
import java.util.Map;

/**
 * Enumeration for the different types of JSON instances which can be
 * encountered.
 *
 * <p>In addition to what the JSON RFC defines, JSON Schema has an {@code
 * integer} type, which is a numeric value without any fraction or exponent
 * part.</p>
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
    NULL("null"),
    /**
     * Object nodes
     */
    NUMBER("number"),
    /**
     * Null nodes
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

    /**
     * Reverse map to find a node type out of this type's name
     */
    private static final Map<String, NodeType> NAME_MAP;

    /**
     * Mapping of {@link JsonToken} back to node types (used in {@link
     * #getNodeType(JsonNode)})
     */
    private static final Map<JsonToken, NodeType> TOKEN_MAP
        = new EnumMap<JsonToken, NodeType>(JsonToken.class);

    static {
        TOKEN_MAP.put(JsonToken.START_ARRAY, ARRAY);
        TOKEN_MAP.put(JsonToken.VALUE_TRUE, BOOLEAN);
        TOKEN_MAP.put(JsonToken.VALUE_FALSE, BOOLEAN);
        TOKEN_MAP.put(JsonToken.VALUE_NUMBER_INT, INTEGER);
        TOKEN_MAP.put(JsonToken.VALUE_NUMBER_FLOAT, NUMBER);
        TOKEN_MAP.put(JsonToken.VALUE_NULL, NULL);
        TOKEN_MAP.put(JsonToken.START_OBJECT, OBJECT);
        TOKEN_MAP.put(JsonToken.VALUE_STRING, STRING);

        final ImmutableMap.Builder<String, NodeType> builder
            = ImmutableMap.builder();

        for (final NodeType type: NodeType.values())
            builder.put(type.name, type);

        NAME_MAP = builder.build();
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
        return NAME_MAP.get(name);
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
        final NodeType ret = TOKEN_MAP.get(token);

        Preconditions.checkNotNull(ret, "unhandled token type " + token);

        return ret;
    }
}
