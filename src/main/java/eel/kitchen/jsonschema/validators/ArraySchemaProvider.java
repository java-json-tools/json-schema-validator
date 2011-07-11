package eel.kitchen.jsonschema.validators;

import org.codehaus.jackson.JsonNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ArraySchemaProvider
    implements SchemaProvider
{
    private final Map<String, JsonNode> items
        = new HashMap<String, JsonNode>();

    private final JsonNode additionalItems;

    public ArraySchemaProvider(final List<JsonNode> itemList,
        final JsonNode additionalItems)
    {
        int i = 0;

        for (final JsonNode item: itemList)
            items.put(String.format("[%d]", i++), item);

        this.additionalItems = additionalItems;
    }

    @Override
    public JsonNode getSchemaForPath(final String path)
    {
        return items.containsKey(path) ? items.get(path) : additionalItems;
    }
}
