package eel.kitchen.jsonschema.validators;

import org.codehaus.jackson.JsonNode;

public interface SchemaProvider
{
    JsonNode getSchemaForPath(final String path);
}
