package eel.kitchen.jsonschema;

import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class JasonSchemaTest
{
    private JsonNode node;
    private JasonSchema schema;
    private List<String> messages;

    @Test
    public void testDynDB()
        throws IOException
    {
        node = JasonHelper.load("fullschemas/dyndb.json");
        schema = new JasonSchema(node.get("schema"));

        assertFalse(schema.validate(node.get("ko")));
        messages = schema.getValidationErrors();
        assertEquals(messages.size(), 2);
        assertEquals(messages.get(0), "$.table1: property id is required but "
            + "was not found");
        assertEquals(messages.get(1), "$.table2.croute.column: node is of "
            + "type boolean, expected string");

        assertTrue(schema.validate(node.get("ok")));
        messages = schema.getValidationErrors();
        assertTrue(messages.isEmpty());
    }

    @Test
    public void test2()
        throws IOException
    {
        node = JasonHelper.load("fullschemas/test2.json");
        schema = new JasonSchema(node.get("schema"));

        assertFalse(schema.validate(node.get("ko")));
        messages = schema.getValidationErrors();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "$: additional properties were found "
            + "but schema forbids them");

        assertTrue(schema.validate(node.get("ok")));
        messages = schema.getValidationErrors();
        assertTrue(messages.isEmpty());
    }

    @Test
    public void test3()
        throws IOException
    {
        node = JasonHelper.load("fullschemas/test3.json");
        schema = new JasonSchema(node.get("schema"));

        assertFalse(schema.validate(node.get("ko")));
        messages = schema.getValidationErrors();
        assertEquals(messages.size(), 3);
        assertEquals(messages.get(0), "$.[0]: node is of type boolean, "
            + "expected string");
        assertEquals(messages.get(1), "$.[1]: property spirit depends on "
            + "elevated, but the latter was not found");
        assertEquals(messages.get(2), "$.[2]: node is of type string, "
            + "expected integer");

        assertTrue(schema.validate(node.get("ok")));
        messages = schema.getValidationErrors();
        assertTrue(messages.isEmpty());
    }

    @Test
    public void test4()
        throws IOException
    {
        node = JasonHelper.load("fullschemas/test4.json");
        schema = new JasonSchema(node.get("schema"));

        assertFalse(schema.validate(node.get("ko")));
        messages = schema.getValidationErrors();
        assertEquals(messages.size(), 3);
        assertEquals(messages.get(0), "$.[0]: node is of type boolean, "
            + "expected one of [string, integer]");
        assertEquals(messages.get(1), "$.[1]: integer is not a multiple of "
            + "the declared divisor");
        assertEquals(messages.get(2), "$.[2]: string is not a valid date");

        assertTrue(schema.validate(node.get("ok")));
        messages = schema.getValidationErrors();
        assertTrue(messages.isEmpty());
    }
}
