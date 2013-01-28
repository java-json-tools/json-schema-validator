package com.github.fge.jsonschema.util.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * Utility class for Jackson
 *
 * <p>This class provides a custom {@link JsonNodeFactory} and {@link
 * ObjectReader} which you should use preferably to your own (in particular,
 * the reader ensures that decimal values are read using {@link BigDecimal}.</p>
 *
 * <p>It also provides a method for returning an empty {@link ObjectNode}
 * (for all practical purposes, an empty schema), and one to convert a JSON
 * object into a {@link Map}.</p>
 */
public final class JacksonUtils
{
    private static final JsonNodeFactory FACTORY
        = JsonNodeFactory.withExactBigDecimals(false);

    private static final JsonNode EMPTY_OBJECT = FACTORY.objectNode();

    private static final ObjectReader READER = new ObjectMapper()
        .setNodeFactory(FACTORY)
        .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS).reader();

    private JacksonUtils()
    {
    }

    /**
     * Return a preconfigured {@link ObjectReader} to read JSON inputs
     *
     * @return the reader
     */
    public static ObjectReader getReader()
    {
        return READER;
    }

    /**
     * Return a preconfigured {@link JsonNodeFactory} to generate JSON data as
     * {@link JsonNode}s
     *
     * @return the factory
     */
    public static JsonNodeFactory nodeFactory()
    {
        return FACTORY;
    }

    /**
     * Return a statically created {@link ObjectNode} instance
     *
     * <p>NOTE: this instance is MUTABLE, but right now Jackson provides no way
     * to create immutable object nodes.</p>
     *
     * @return an empty object node
     */
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
