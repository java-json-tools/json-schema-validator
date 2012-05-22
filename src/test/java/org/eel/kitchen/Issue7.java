package org.eel.kitchen;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.schema.JsonSchema;
import org.eel.kitchen.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

public final class Issue7
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
        final JsonSchema schema = JsonSchema.fromNode(draftv3);
        ValidationReport report;

        report = new ValidationReport();
        schema.validate(report, schema1);
        assertTrue(report.isSuccess());

        final JsonSchema temp1schema = JsonSchema.fromNode(schema1);

        /**
         * The bug is here: normally, validation should fail because
         * "indexed" is not a boolean. But it succeeds... The reason for this
         * is that JsonSchema's .objectPath() returns an empty schema: it
         * shouldn't!
         */
        report = new ValidationReport();
        temp1schema.validate(report, schema2);
        assertFalse(report.isSuccess());
    }
}
