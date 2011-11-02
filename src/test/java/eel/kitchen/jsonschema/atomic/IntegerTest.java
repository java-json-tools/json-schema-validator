package eel.kitchen.jsonschema.atomic;

import eel.kitchen.jsonschema.JsonValidator;
import eel.kitchen.jsonschema.ValidationReport;
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.testng.Assert.*;

public final class IntegerTest
{
    private JsonNode testNode;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        testNode = JasonHelper.load("/atomic/integer.json");
    }

    @Test
    public void testMinimum()
    {
        testOne("minimum");
    }

    @Test
    public void testExclusiveMinimum()
    {
        testOne("exclusiveMinimum");
    }

    @Test
    public void testMaximum()
    {
        testOne("maximum");
    }

    @Test
    public void testExclusiveMaximum()
    {
        testOne("exclusiveMaximum");
    }

    @Test
    public void testDivisibleBy()
    {
        testOne("divisibleBy");
    }
    private void testOne(final String testName)
    {
        final JsonNode node = testNode.get(testName);
        final JsonNode schema = node.get("schema");
        final JsonNode good = node.get("good");
        final JsonNode bad = node.get("bad");

        final JsonValidator validator = new JsonValidator(schema);

        ValidationReport report = validator.validate(good);

        assertTrue(report.isSuccess());
        assertTrue(report.getMessages().isEmpty());

        final List<String> expected = new LinkedList<String>();

        for (final JsonNode element: node.get("messages"))
            expected.add(element.getTextValue());

        report = validator.validate(bad);

        assertFalse(report.isSuccess());

        assertEquals(report.getMessages(), expected);
    }
}
