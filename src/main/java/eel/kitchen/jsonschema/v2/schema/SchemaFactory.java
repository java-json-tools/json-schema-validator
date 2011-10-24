package eel.kitchen.jsonschema.v2.schema;

import eel.kitchen.jsonschema.v2.check.SchemaChecker;
import eel.kitchen.jsonschema.v2.instance.Instance;
import org.codehaus.jackson.JsonNode;

import java.util.Collections;
import java.util.List;

public final class SchemaFactory
{
    private static final SchemaChecker checker = SchemaChecker.getInstance();

    public Schema getSchema(final JsonNode schema)
    {
        final List<String> messages = checker.check(schema);

        if (!messages.isEmpty())
            return failure(messages);

        //TODO: implement
        return null;
    }

    private Schema failure(final List<String> messages)
    {
        return new Schema()
        {
            @Override
            public JsonNode getRawSchema()
            {
                return null;
            }

            @Override
            public boolean canExpand()
            {
                return false;
            }

            @Override
            public Schema getSchema(final String path)
            {
                return null;
            }

            @Override
            public boolean validate(final Instance instance)
            {
                return false;
            }

            @Override
            public List<String> getMessages()
            {
                return Collections.unmodifiableList(messages);
            }
        };

    }
}
