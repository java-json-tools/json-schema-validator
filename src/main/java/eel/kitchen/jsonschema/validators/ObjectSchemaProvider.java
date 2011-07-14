package eel.kitchen.jsonschema.validators;

import eel.kitchen.util.RhinoHelper;
import org.codehaus.jackson.JsonNode;

import java.util.HashMap;
import java.util.Map;

public final class ObjectSchemaProvider
    implements SchemaProvider
{
    private final Map<String, JsonNode> properties
        = new HashMap<String, JsonNode>();
    private final Map<String, JsonNode> patternProperties
        = new HashMap<String, JsonNode>();
    private final JsonNode additionalProperties;

    public ObjectSchemaProvider(final Map<String, JsonNode> properties,
        final Map<String, JsonNode> patternProperties,
        final JsonNode additionalProperties)
    {
        this.properties.putAll(properties);
        this.patternProperties.putAll(patternProperties);
        this.additionalProperties = additionalProperties;
    }

    @Override
    public JsonNode getSchemaForPath(final String path)
    {
        if (properties.containsKey(path))
            return properties.get(path);

        for (final String regex: patternProperties.keySet())
            if (RhinoHelper.regMatch(regex, path))
                return patternProperties.get(regex);

        return additionalProperties;
    }
}
