package eel.kitchen.jsonschema.v2.schema;

import org.codehaus.jackson.JsonNode;

public final class AtomicNodePathProvider
    implements PathProvider
{
    private static final AtomicNodePathProvider instance
        = new AtomicNodePathProvider();

    private AtomicNodePathProvider()
    {
    }

    public static AtomicNodePathProvider getInstance()
    {
        return instance;
    }

    @Override
    public JsonNode getSchema(final String path)
    {
        throw new RuntimeException("Tried to get a subschema for a non "
            + "container type instance validation");
    }
}
