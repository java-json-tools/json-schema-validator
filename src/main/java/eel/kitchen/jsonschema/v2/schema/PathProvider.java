package eel.kitchen.jsonschema.v2.schema;

import org.codehaus.jackson.JsonNode;

interface PathProvider
{
    JsonNode getSchema(final String path);
}
