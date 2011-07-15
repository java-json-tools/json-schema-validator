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
