package org.eel.kitchen.jsonschema;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.schema.JsonSchema;
import org.eel.kitchen.jsonschema.schema.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.*;

public class AndyHeathTest
{
    private JsonSchemaFactory factory;
    private JsonNode schemaNode;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        factory = new JsonSchemaFactory();
        schemaNode = JsonLoader.fromResource("/andy-heath-schema.json");
    }

    @DataProvider
    public Iterator<Object[]> getData()
        throws IOException
    {
        final JsonNode data = JsonLoader.fromResource("/andy-heath-data.json");
        final Set<Object[]> set = new HashSet<Object[]>();

        for (final JsonNode node: data)
            set.add(new Object[] { node });

        return set.iterator();
    }

    @Test(dataProvider = "getData")
    public void AndyHeathTestCaseYieldsExpectedResult(final JsonNode node)
    {
        /*
         * Validation succeeds
         */
        final JsonSchema validator = factory.create(schemaNode);
        final ValidationContext ctx = factory.newContext();

        validator.validate(ctx, node);
        System.out.println(ctx.getMessages());
        assertTrue(ctx.isSuccess(), "validation should have succeeded");
    }
}
