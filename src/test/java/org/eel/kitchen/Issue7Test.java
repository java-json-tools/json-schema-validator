package org.eel.kitchen;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.schema.AbstractJsonSchema;
import org.eel.kitchen.jsonschema.schema.JsonSchema;
import org.eel.kitchen.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

public final class Issue7Test
{
    private JsonNode draftv3, schema1, schema2;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        draftv3 = JsonLoader.fromResource("/schema-draftv3.json");
        schema1 = JsonLoader.fromResource("/schema1.json");
        schema2 = JsonLoader.fromResource("/schema2.json");

    }

    @Test
    public void testIssue7()
    {
        final JsonSchema schema = AbstractJsonSchema.fromNode(draftv3);
        ValidationContext context;

        context = new ValidationContext();
        schema.validate(context, schema1);
        assertTrue(context.isSuccess());

        final JsonSchema temp1schema = AbstractJsonSchema.fromNode(schema1);

        /**
         * The bug is here: normally, validation should fail because
         * "indexed" is not a boolean. But it succeeds... The reason for this
         * is that JsonSchema's .objectPath() returns an empty schema: it
         * shouldn't!
         */
        context = new ValidationContext();
        temp1schema.validate(context, schema2);
        assertFalse(context.isSuccess());
    }
}
