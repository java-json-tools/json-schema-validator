package eel.kitchen.jsonschema.validators;

import org.codehaus.jackson.JsonNode;

public final class EmptySchemaProvider
    implements SchemaProvider
{
    @Override
    public JsonNode getSchemaForPath(final String path)
    {
        throw new RuntimeException("I should never be called!!");
    }
}
