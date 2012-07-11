package org.eel.kitchen;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

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
//        final JsonValidator validator = AbstractJsonValidator.fromNode(draftv3);
//        ValidationContext context;
//
//        context = new ValidationContext();
//        validator.validate(context, schema1);
//        assertTrue(context.isSuccess());
//
//        final JsonValidator temp1schema = AbstractJsonValidator.fromNode(
//            schema1);
//
//        /**
//         * The bug is here: normally, validation should fail because
//         * "indexed" is not a boolean. But it succeeds... The reason for this
//         * is that JsonValidator's .objectPath() returns an empty validator: it
//         * shouldn't!
//         */
//        context = new ValidationContext();
//        temp1schema.validate(context, schema2);
//        assertFalse(context.isSuccess());
    }
}
