/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.util;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.node.MissingNode;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * Enumeration for the different types of JSON instances which can be
 * encountered.
 */

public enum NodeType
{
    /**
     * Array nodes
     */
    ARRAY("array", JsonToken.START_ARRAY),
    /**
     * Boolean nodes
     */
    BOOLEAN("boolean", JsonToken.VALUE_TRUE, JsonToken.VALUE_FALSE),
    /**
     * Integer nodes
     */
    INTEGER("integer", JsonToken.VALUE_NUMBER_INT),
    /**
     * Number nodes (ie, decimal numbers)
     */
    NUMBER("number", JsonToken.VALUE_NUMBER_FLOAT),
    /**
     * Null nodes
     */
    NULL("null", JsonToken.VALUE_NULL),
    /**
     * Object nodes
     */
    OBJECT("object", JsonToken.START_OBJECT),
    /**
     * String nodes
     */
    STRING("string", JsonToken.VALUE_STRING);

    /**
     * The name for this type, as encountered in a JSON schema
     */
    private final String name;

    /**
     * Expected {@link JsonToken} values for this type
     */
    private final EnumSet<JsonToken> tokens;

    NodeType(final String name, final JsonToken... tokenlist)
    {
        this.name = name;
        tokens = EnumSet.copyOf(Arrays.asList(tokenlist));
    }

    @Override
    public String toString()
    {
        return name;
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

        for (final NodeType type: values())
            if (type.tokens.contains(token))
                return type;

        throw new IllegalArgumentException("unhandled token type " + token);
    }
}
