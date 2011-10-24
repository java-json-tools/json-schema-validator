package eel.kitchen.jsonschema.v2.schema;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;

import java.util.LinkedList;
import java.util.List;

public final class ArrayPathProvider
    implements PathProvider
{
    private static final JsonNode EMPTY_SCHEMA
        = JsonNodeFactory.instance.objectNode();

    private final List<JsonNode> items = new LinkedList<JsonNode>();

    private final JsonNode additionalItems;

    public ArrayPathProvider(final JsonNode schema)
    {
        JsonNode node = schema.path("items");

        if (node.isObject()) {
            additionalItems = node;
            return;
        }

        if (node.isArray()) {
            for (final JsonNode item: node)
                items.add(item);
        }

        node = schema.path("additionalItems");

        additionalItems = node.isObject() ? node : EMPTY_SCHEMA;
    }

    @Override
    public JsonNode getSchema(final String path)
    {
        final int index;
        try {
            index = Integer.parseInt(path);
            if (index < 0)
                throw new NumberFormatException("index is negative");
        } catch (NumberFormatException e) {
            throw new RuntimeException("Tried to access schema for array "
                + "instance element with an illegal index (" + path + ")", e);
        }

        try {
            return items.get(index);
        } catch (IndexOutOfBoundsException ignored) {
            return additionalItems;
        }
    }
}
