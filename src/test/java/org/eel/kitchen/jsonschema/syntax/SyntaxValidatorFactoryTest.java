/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.syntax;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.JsonValidator;
import org.eel.kitchen.jsonschema.main.ValidationConfig;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.util.JsonLoader;
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

    private JsonNode allTests;
    private ValidationReport report;
    private JsonValidator validator;
    private final ValidationConfig cfg = new ValidationConfig();

    @BeforeClass
    public void setUp()
        throws IOException
    {
        allTests = JsonLoader.fromResource("/syntax/syntax.json");
    }

    @Test
    public void testNullSchema()
    {
        try {
            validator = new JsonValidator(cfg, null);
            fail("No exception thrown");
        } catch (JsonValidationFailureException e) {
            assertEquals(e.getMessage(), "schema is null");
        }
    }

    @Test
    public void testEmptySchema()
        throws JsonValidationFailureException
    {
        validator = new JsonValidator(cfg, nodeFactory.objectNode());
        report = validator.validateSchema();

        assertTrue(report.isSuccess());

        assertTrue(report.getMessages().isEmpty());
    }

    @Test
    public void testNonObjectSchema()
    {
        try {
            validator = new JsonValidator(cfg, nodeFactory.textNode("hello"));
            fail("No exception thrown");
        } catch (JsonValidationFailureException e) {
            assertEquals(e.getMessage(), "not a schema (not an object)");
        }
    }

    @Test
    public void testUnknownKeyword()
        throws JsonValidationFailureException
    {
        final ObjectNode schema = nodeFactory.objectNode();
        schema.put("toto", 2);

        validator = new JsonValidator(cfg, schema);

        report = validator.validateSchema();

        assertFalse(report.isSuccess());
        final List<String> messages = report.getMessages();

        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "# [schema]: unknown keyword toto");
    }

    @Test
    public void testAdditionalItems()
        throws JsonValidationFailureException
    {
        testKeyword("additionalItems");
    }

    @Test
    public void testAdditionalProperties()
        throws JsonValidationFailureException
    {
        testKeyword("additionalProperties");
    }

    @Test
    public void testDependencies()
        throws JsonValidationFailureException
    {
        testKeyword("dependencies");
    }

    @Test
    public void testDescription()
        throws JsonValidationFailureException
    {
        testKeyword("description");
    }

    @Test
    public void testDisallow()
        throws JsonValidationFailureException
    {
        testKeyword("disallow");
    }

    @Test
    public void testDivisibleBy()
        throws JsonValidationFailureException
    {
        testKeyword("divisibleBy");
    }

    @Test
    public void testDollarRef()
        throws JsonValidationFailureException
    {
        testKeyword("$ref");
    }

    @Test
    public void testEnum()
        throws JsonValidationFailureException
    {
        testKeyword("enum");
    }

    @Test
    public void testExclusiveMaximum()
        throws JsonValidationFailureException
    {
        testKeyword("exclusiveMaximum");
    }

    @Test
    public void testExclusiveMinimum()
        throws JsonValidationFailureException
    {
        testKeyword("exclusiveMinimum");
    }

    @Test
    public void testExtends()
        throws JsonValidationFailureException
    {
        testKeyword("extends");
    }

    @Test
    public void testFormat()
        throws JsonValidationFailureException
    {
        testKeyword("format");
    }

    @Test
    public void testId()
        throws JsonValidationFailureException
    {
        testKeyword("id");
    }

    @Test
    public void testItems()
        throws JsonValidationFailureException
    {
        testKeyword("items");
    }

    @Test
    public void testMaximum()
        throws JsonValidationFailureException
    {
        testKeyword("maximum");
    }

    @Test
    public void testMaxItems()
        throws JsonValidationFailureException
    {
        testKeyword("maxItems");
    }

    @Test
    public void testMaxLength()
        throws JsonValidationFailureException
    {
        testKeyword("maxLength");
    }

    @Test
    public void testMinimum()
        throws JsonValidationFailureException
    {
        testKeyword("minimum");
    }

    @Test
    public void testMinItems()
        throws JsonValidationFailureException
    {
        testKeyword("minItems");
    }

    @Test
    public void testMinLength()
        throws JsonValidationFailureException
    {
        testKeyword("minLength");
    }

    @Test
    public void testPatternProperties()
        throws JsonValidationFailureException
    {
        testKeyword("patternProperties");
    }

    @Test
    public void testPattern()
        throws JsonValidationFailureException
    {
        testKeyword("pattern");
    }

    @Test
    public void testProperties()
        throws JsonValidationFailureException
    {
        testKeyword("properties");
    }

    @Test
    public void testTitle()
        throws JsonValidationFailureException
    {
        testKeyword("title");
    }

    @Test
    public void testType()
        throws JsonValidationFailureException
    {
        testKeyword("type");
    }

    @Test
    public void testUniqueItems()
        throws JsonValidationFailureException
    {
        testKeyword("uniqueItems");
    }

    private void testKeyword(final String keyword)
        throws JsonValidationFailureException
    {
        final JsonNode node = allTests.get(keyword);

        for (final JsonNode element: node)
            testEntry(element);
    }

    private void testEntry(final JsonNode element)
        throws JsonValidationFailureException
    {
        final JsonNode schema = element.get("schema");
        final boolean valid = element.get("valid").getBooleanValue();

        validator = new JsonValidator(cfg, schema);

        try {
            report = validator.validateSchema();
        } catch (JsonValidationFailureException ignored) {
            fail();
        }

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
            "message list differs from expectations while validating "
                + schema);
    }
}
