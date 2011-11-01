/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.syntax;

import eel.kitchen.jsonschema.ValidationReport;
import eel.kitchen.jsonschema.base.Validator;
import eel.kitchen.jsonschema.context.ValidationContext;
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.testng.Assert.*;

//TODO: test reflection issues

public final class SyntaxValidatorFactoryTest
{
    private static final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
    private static final JsonNode dummy = nodeFactory.nullNode();

    private JsonNode allTests;
    private ValidationContext context;
    private Validator v;
    private ValidationReport report;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        allTests = JasonHelper.load("/syntax/syntax.json");
    }

    @Test
    public void testNullSchema()
    {
        context = new ValidationContext(null);
        v = context.getValidator(dummy);
        report = v.validate();

        assertFalse(report.isSuccess());

        final List<String> messages = report.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "# [schema]: schema is null");
    }

    @Test
    public void testEmptySchema()
    {
        final JsonNode schema = nodeFactory.objectNode();

        context = new ValidationContext(schema);
        v = context.getValidator(schema);
        report = new ValidationReport();

        assertTrue(report.isSuccess());

        assertTrue(report.getMessages().isEmpty());
    }

    @Test
    public void testNonObjectSchema()
    {
        final JsonNode schema = nodeFactory.textNode("hello");

        context = new ValidationContext(schema);
        v = context.getValidator(schema);
        report = v.validate();

        assertFalse(report.isSuccess());

        final List<String> messages = report.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "# [schema]: not a valid schema (not an"
            + " object)");
    }

    @Test
    public void testUnknownKeyword()
    {
        final ObjectNode schema = nodeFactory.objectNode();
        schema.put("toto", 2);

        context = new ValidationContext(schema);
        v = context.getValidator(schema);
        report = v.validate();

        assertFalse(report.isSuccess());
        final List<String> messages = report.getMessages();

        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "# [schema]: unknown keyword toto");
    }

    @Test
    public void testAdditionalItems()
    {
        testKeyword("additionalItems");
    }

    @Test
    public void testAdditionalProperties()
    {
        testKeyword("additionalProperties");
    }

    @Test
    public void testDependencies()
    {
        testKeyword("dependencies");
    }

    @Test
    public void testDescription()
    {
        testKeyword("description");
    }

    @Test
    public void testDisallow()
    {
        testKeyword("disallow");
    }

    @Test
    public void testDivisibleBy()
    {
        testKeyword("divisibleBy");
    }

    @Test
    public void testDollarRef()
    {
        testKeyword("$ref");
    }

    @Test
    public void testDollarSchema()
    {
        testKeyword("$schema");
    }

    @Test
    public void testEnum()
    {
        testKeyword("enum");
    }

    @Test
    public void testExclusiveMaximum()
    {
        testKeyword("exclusiveMaximum");
    }

    @Test
    public void testExclusiveMinimum()
    {
        testKeyword("exclusiveMinimum");
    }

    @Test
    public void testExtends()
    {
        testKeyword("extends");
    }

    @Test
    public void testFormat()
    {
        testKeyword("format");
    }

    @Test
    public void testId()
    {
        testKeyword("id");
    }

    @Test
    public void testItems()
    {
        testKeyword("items");
    }

    @Test
    public void testMaximum()
    {
        testKeyword("maximum");
    }

    @Test
    public void testMaxItems()
    {
        testKeyword("maxItems");
    }

    @Test
    public void testMaxLength()
    {
        testKeyword("maxLength");
    }

    @Test
    public void testMinimum()
    {
        testKeyword("minimum");
    }

    @Test
    public void testMinItems()
    {
        testKeyword("minItems");
    }

    @Test
    public void testMinLength()
    {
        testKeyword("minLength");
    }

    @Test
    public void testPatternProperties()
    {
        testKeyword("patternProperties");
    }

    @Test
    public void testPattern()
    {
        testKeyword("pattern");
    }

    @Test
    public void testProperties()
    {
        testKeyword("properties");
    }

    @Test
    public void testTitle()
    {
        testKeyword("title");
    }

    @Test
    public void testType()
    {
        testKeyword("type");
    }

    @Test
    public void testUniqueItems()
    {
        testKeyword("uniqueItems");
    }

    private void testKeyword(final String keyword)
    {
        final JsonNode node = allTests.get(keyword);

        for (final JsonNode element: node)
            testEntry(element);
    }

    private void testEntry(final JsonNode element)
    {
        final JsonNode schema = element.get("schema");
        final boolean valid = element.get("valid").getBooleanValue();

        context = new ValidationContext(schema);
        v = context.getValidator(schema);
        report = v.validate();

        if (valid) {
            assertTrue(report.isSuccess(), "schema " + schema + " considered "
                + "invalid");
            assertTrue(report.getMessages().isEmpty());
            return;
        }

        final List<String> expected = new LinkedList<String>();

        for (final JsonNode message: element.get("messages"))
            expected.add(message.getTextValue());

        assertEquals(report.getMessages().toArray(), expected.toArray(),
            "message list differs from expectations while validating " + schema);
    }
}
