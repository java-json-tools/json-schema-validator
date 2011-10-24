package eel.kitchen.jsonschema.v2.schema;

import eel.kitchen.jsonschema.v2.instance.Instance;
import org.codehaus.jackson.JsonNode;

import java.util.List;

public interface Schema
{
    JsonNode getRawSchema();

    boolean canExpand();

    Schema getSchema(final String path);

    boolean validate(final Instance instance);

    List<String> getMessages();
}
