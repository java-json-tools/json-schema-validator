package eel.kitchen.jsonschema;

import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import org.codehaus.jackson.JsonNode;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class JasonSchema2Test
{
    private JsonNode node;
    private JasonSchema2 schema;
    private List<String> messages;

    @Test
    public void testDynDB()
        throws IOException
    {
        node = JasonLoader.load("fullschemas/dyndb.json");
        schema = new JasonSchema2(node.get("schema"));

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
        node = JasonLoader.load("fullschemas/test2.json");
        schema = new JasonSchema2(node.get("schema"));

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
        node = JasonLoader.load("fullschemas/test3.json");
        schema = new JasonSchema2(node.get("schema"));

        assertFalse(schema.validate(node.get("ko")));
        messages = schema.getValidationErrors();
        assertEquals(messages.size(), 3);
        assertEquals(messages.get(0), "$[0]: node is of type boolean, "
            + "expected string");
        assertEquals(messages.get(1), "$[1]: property spirit depends on "
            + "elevated, but the latter was not found");
        assertEquals(messages.get(2), "$[2]: node is of type string, "
            + "expected integer");

        assertTrue(schema.validate(node.get("ok")));
        messages = schema.getValidationErrors();
        assertTrue(messages.isEmpty());
    }
}
