package eel.kitchen.jsonschema.validators.misc;

import eel.kitchen.jsonschema.validators.Validator;
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public final class RequiredValidatorTest
{
    private JsonNode testNode;
    private final Validator v = new RequiredValidator();
    private final List<String> reference = Arrays.asList(
        "property p3 is required but was not found",
        "property p4 is required but was not found"
    );
    private List<String> messages;

    @BeforeClass
    public void setup()
        throws IOException
    {
        testNode = JasonHelper.load("required.json");
    }

    @Test(priority = 0)
    public void testBroken()
    {
        v.setSchema(testNode.get("broken"));

        assertFalse(v.setup());
        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "required should be a boolean");
    }

    @Test(priority = 1)
    public void testValidation()
    {
        v.setSchema(testNode.get("schema"));

        assertTrue(v.setup());

        assertTrue(v.validate(testNode.get("good")));

        assertFalse(v.validate(testNode.get("bad")));
        messages = new ArrayList<String>(v.getMessages());
        assertEquals(messages.size(), 2);
        messages.removeAll(reference);
        assertTrue(messages.isEmpty());
    }
}
