package eel.kitchen.jsonschema.v2.schema;

import eel.kitchen.util.CollectionUtils;
import eel.kitchen.util.RhinoHelper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;

import java.util.HashMap;
import java.util.Map;

public final class ObjectPathProvider
    implements PathProvider
{
    private static final JsonNode EMPTY_SCHEMA
        = JsonNodeFactory.instance.objectNode();

    private static final Map<String, JsonNode> properties
        = new HashMap<String, JsonNode>();

    private static final Map<String, JsonNode> patternProperties
        = new HashMap<String, JsonNode>();

    private final JsonNode additionalProperties;

    public ObjectPathProvider(final JsonNode schema)
    {
        JsonNode node = schema.path("properties");

        if (node.isObject())
            properties.putAll(CollectionUtils.toMap(node.getFields()));

        node = schema.path("patternProperties");

        if (node.isObject())
            patternProperties.putAll(CollectionUtils.toMap(node.getFields()));

        node = schema.path("additionalProperties");

        additionalProperties = node.isObject() ? node : EMPTY_SCHEMA;
    }

    @Override
    public JsonNode getSchema(final String path)
    {
        if (properties.containsKey(path))
            return properties.get(path);

        for (final String pattern: patternProperties.keySet())
            if (RhinoHelper.regMatch(pattern, path))
                return patternProperties.get(pattern);

        return additionalProperties;
    }
}
