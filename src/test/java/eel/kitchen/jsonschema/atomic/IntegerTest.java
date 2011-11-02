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

    private JsonValidator validator;
    private ValidationReport report;
    private JsonNode node, schema, good, bad;
    private final List<String>
        messages = new LinkedList<String>(),
        expected = new LinkedList<String>();

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

    private void testOne(final String testName)
    {
        node = testNode.get(testName);
        schema = node.get("schema");
        good = node.get("good");
        bad = node.get("bad");

        validator = new JsonValidator(schema);

        report = validator.validate(good);

        assertTrue(report.isSuccess());
        assertTrue(report.getMessages().isEmpty());

        messages.clear();
        expected.clear();

        for (final JsonNode element: node.get("messages"))
            expected.add(element.getTextValue());

        report = validator.validate(bad);

        assertFalse(report.isSuccess());

        messages.addAll(report.getMessages());

        assertEquals(messages, expected);
    }
}
