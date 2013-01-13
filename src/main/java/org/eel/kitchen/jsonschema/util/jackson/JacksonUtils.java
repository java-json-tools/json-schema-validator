package org.eel.kitchen.jsonschema.util.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * Simple utility class
 */
public final class JacksonUtils
{
    private static final JsonNodeFactory FACTORY
        = JsonNodeFactory.withExactBigDecimals(false);

    private static final JsonNode EMPTY_OBJECT = FACTORY.objectNode();

    private static final ObjectMapper MAPPER = new ObjectMapper()
        .setNodeFactory(FACTORY)
        .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);

    private JacksonUtils()
    {
    }

    public static ObjectMapper getMapper()
    {
        return MAPPER;
    }

    public static JsonNodeFactory nodeFactory()
    {
        return FACTORY;
    }

    public static JsonNode emptyObject()
    {
        return EMPTY_OBJECT;
    }

    /**
     * Return a map out of an object's members
     *
     * <p>If the node given as an argument is not a map, an empty map is
     * returned.</p>
     *
     * @param node the node
     * @return a map
     */
    public static Map<String, JsonNode> asMap(final JsonNode node)
    {
        if (!node.isObject())
            return Collections.emptyMap();

        final Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
        final Map<String, JsonNode> ret = Maps.newHashMap();

        Map.Entry<String, JsonNode> entry;

        while (iterator.hasNext()) {
            entry = iterator.next();
            ret.put(entry.getKey(), entry.getValue());
        }

        return ret;
    }
}
