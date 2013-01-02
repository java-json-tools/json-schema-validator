package org.eel.kitchen.jsonschema.util.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;

public final class JacksonUtils
{
    private JacksonUtils()
    {
    }

    public static Map<String, JsonNode> asMap(final JsonNode node)
    {
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
