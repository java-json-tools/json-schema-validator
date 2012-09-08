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

package org.eel.kitchen.jsonschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.eel.kitchen.jsonschema.util.JacksonUtils;
import org.eel.kitchen.jsonschema.util.JsonProvider;

import java.math.BigDecimal;
import java.util.Map;

public final class GsonProvider
    implements JsonProvider<JsonElement>
{
    private static final JsonProvider<JsonElement> instance
        = new GsonProvider();

    private GsonProvider()
    {
    }

    private static final JsonNodeFactory factory = JsonNodeFactory.instance;

    public static JsonProvider<JsonElement> getInstance()
    {
        return instance;
    }

    @Override
    public JsonNode toJsonNode(final JsonElement input)
    {
        return gsonToJsonNode(input);
    }

    @Override
    public JsonElement fromJsonNode(final JsonNode node)
    {
        return jsonNodeToGson(node);
    }

    @Override
    public Class<JsonElement> getProvidingClass()
    {
        return JsonElement.class;
    }

    /*
     * Jackson to Gson
     */
    private JsonElement jsonNodeToGson(final JsonNode node)
    {
        if (node.isNull())
            return JsonNull.INSTANCE;
        if (node.isTextual())
            return new JsonPrimitive(node.textValue());
        if (node.isBoolean())
            return new JsonPrimitive(node.booleanValue());
        if (node.isNumber())
            return new JsonPrimitive(node.numberValue());

        return node.isArray() ? arrayNodeToGson(node) : objectNodeToGson(node);
    }

    private JsonElement arrayNodeToGson(final JsonNode node)
    {
        final JsonArray ret = new JsonArray();

        for (final JsonNode element: node)
            ret.add(jsonNodeToGson(element));

        return ret;
    }

    private JsonElement objectNodeToGson(final JsonNode node)
    {
        final JsonObject ret = new JsonObject();

        final Map<String, JsonNode> map = JacksonUtils.nodeToMap(node);

        for (final Map.Entry<String, JsonNode> entry: map.entrySet())
            ret.add(entry.getKey(), jsonNodeToGson(entry.getValue()));

        return ret;
    }

    /*
     * Gson to Jackon
     */
    private JsonNode gsonToJsonNode(final JsonElement element)
    {
        if (element.isJsonNull())
            return factory.nullNode();
        if (element.isJsonPrimitive())
            return gsonToValueNode(element.getAsJsonPrimitive());

        return element.isJsonArray() ? gsonToArrayNode(element)
            : gsonToObjectNode(element);
    }

    private JsonNode gsonToArrayNode(final JsonElement element)
    {
        final ArrayNode ret = factory.arrayNode();

        for (final JsonElement e: element.getAsJsonArray())
            ret.add(gsonToJsonNode(e));

        return ret;
    }

    private JsonNode gsonToObjectNode(final JsonElement element)
    {
        final ObjectNode ret = factory.objectNode();

        final JsonObject object = element.getAsJsonObject();

        for (final Map.Entry<String, JsonElement> entry: object.entrySet())
            ret.put(entry.getKey(), gsonToJsonNode(entry.getValue()));

        return ret;
    }

    private static JsonNode gsonToValueNode(final JsonPrimitive primitive)
    {
        if (primitive.isBoolean())
            return factory.booleanNode(primitive.getAsBoolean());
        if (primitive.isNumber())
            return toNumberNode(primitive.getAsBigDecimal());

        // Can only be a string,now
        return factory.textNode(primitive.getAsString());
    }

    private static JsonNode toNumberNode(final BigDecimal decimal)
    {
        try {
            return factory.numberNode(decimal.intValueExact());
        } catch (ArithmeticException ignored) {
            try {
                return factory.numberNode(decimal.longValueExact());
            } catch (ArithmeticException ignoredAgain) {
                return decimal.scale() == 0
                    ? factory.numberNode(decimal.toBigInteger())
                    : factory.numberNode(decimal);
            }
        }
    }
}
