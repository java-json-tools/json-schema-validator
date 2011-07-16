/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.util;

import org.codehaus.jackson.JsonNode;

public enum NodeType
{
    ARRAY("array"),
    BOOLEAN("boolean"),
    INTEGER("integer"),
    NUMBER("number"),
    NULL("null"),
    OBJECT("object"),
    STRING("string");

    private final String name;

    NodeType(final String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public static NodeType getNodeType(final JsonNode node)
    {
        if (node.isArray())
            return ARRAY;
        if (node.isBoolean())
            return BOOLEAN;
        if (node.isNumber())
            return node.isIntegralNumber() ? INTEGER : NUMBER;
        if (node.isNull())
            return NULL;
        if (node.isObject())
            return OBJECT;

        return STRING;
    }
}
