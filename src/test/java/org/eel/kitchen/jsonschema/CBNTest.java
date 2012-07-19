package org.eel.kitchen.jsonschema;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.schema.JsonSchema;
import org.eel.kitchen.jsonschema.schema.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

public class CBNTest
{
    private JsonSchemaFactory factory;
    private JsonNode schemaNode;
    private JsonNode data;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        factory = new JsonSchemaFactory();
        schemaNode = JsonLoader.fromResource("/cbn-schema.json");
        data = JsonLoader.fromResource("/cbn-data.json");
    }

    @Test
    public void CBNTestCaseYieldsExpectedResult()
    {
        /*
         * Validation should fail because of enum
         */
        final JsonSchema validator = factory.create(schemaNode);
        final ValidationContext ctx = factory.newContext();

        validator.validate(ctx, data);
        assertFalse(ctx.isSuccess(), "validation should have failed");
    }
}
