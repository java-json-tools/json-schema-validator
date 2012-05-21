package org.eel.kitchen;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.schema.JsonSchema;
import org.eel.kitchen.util.JsonLoader;

import java.io.IOException;

public final class Issue7
{
    public static void main(final String... args)
        throws IOException
    {
        final JsonNode draftv3 = JsonLoader.fromResource("/schema-draftv3.json");
        final JsonNode schema1 = JsonLoader.fromResource("/schema1.json");
        final JsonNode schema2 = JsonLoader.fromResource("/schema2.json");

        final JsonSchema schema = JsonSchema.fromNode(draftv3);
        ValidationReport valReport;

        System.out.println("TEST 1");
        valReport = new ValidationReport();
        schema.validate(valReport, schema1);
        for (final String msg : valReport.getMessages())
            System.out.println(msg);

        final JsonSchema temp1schema = JsonSchema.fromNode(schema1);

        System.out.println("TEST 2");
        /**
         * The bug is here: normally, validation should fail because
         * "indexed" is not a boolean. But it succeeds... The reason for this
         * is that JsonSchema's .objectPath() returns an empty schema: it
         * shouldn't!
         */
        valReport = new ValidationReport();
        temp1schema.validate(valReport, schema2);
        for (final String msg : valReport.getMessages())
            System.out.println(msg);
    }
}
