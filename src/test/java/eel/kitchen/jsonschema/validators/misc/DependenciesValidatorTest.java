package eel.kitchen.jsonschema.validators.misc;

import eel.kitchen.jsonschema.validators.Validator;
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public final class DependenciesValidatorTest
{
    private JsonNode brokenSchemas, schema;
    private JsonNode testNode;
    private Validator v;
    private List<String> messages;

    @BeforeClass
    public void setup()
        throws IOException
    {
        testNode = JasonHelper.load("dependencies.json");
        brokenSchemas = testNode.get("broken");
    }

    @Test
    public void testDependencyValidator()
    {
        schema = testNode.get("schema");
        v = new DependenciesValidator();

        final List<String> ref = Arrays.asList(
            "property p1 depends on p3, but the latter was not found",
            "property p1 depends on p4, but the latter was not found"
        );

        v.setSchema(schema);
        assertTrue(v.setup());
        assertTrue(v.getMessages().isEmpty());

        assertTrue(v.validate(testNode.get("good")));
        assertTrue(v.getMessages().isEmpty());

        assertFalse(v.validate(testNode.get("bad")));
        assertEquals(v.getMessages().size(), 2);
        assertTrue(v.getMessages().containsAll(ref));
    }

    @Test
    public void testInvalidType()
    {
        schema = brokenSchemas.get("invalid-type");
        v = new DependenciesValidator().setSchema(schema);

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "dependencies is of type null, "
            + "expected [object]");
    }

    @Test
    public void testInvalidValue()
    {
        schema = brokenSchemas.get("invalid-value");
        v = new DependenciesValidator().setSchema(schema);

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "dependency value should be a string "
            + "or an array");
    }

    @Test
    public void testInvalidElement()
    {
        schema = brokenSchemas.get("invalid-element");
        v = new DependenciesValidator().setSchema(schema);

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "dependency array elements should be "
            + "strings");
    }
}
