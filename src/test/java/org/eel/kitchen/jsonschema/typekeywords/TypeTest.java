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

package org.eel.kitchen.jsonschema.typekeywords;

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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.testng.Assert.*;

public final class TypeTest
{
    private static final JsonNodeFactory factory = JsonNodeFactory.instance;

    private JsonNode testNode;
    private final ValidationConfig cfg = new ValidationConfig();

    @BeforeClass
    public void setUp()
        throws IOException
    {
        testNode = JsonLoader.fromResource("/typekeywords/type.json");
    }

    @Test
    public void testNoTypeKeywordMatchesAll()
        throws JsonValidationFailureException
    {
        final JsonValidator validator = new JsonValidator(cfg,
            factory.objectNode());

        ValidationReport report;

        report = validator.validate(factory.arrayNode());
        assertTrue(report.isSuccess());
        assertTrue(report.getMessages().isEmpty());

        report = validator.validate(factory.booleanNode(true));
        assertTrue(report.isSuccess());
        assertTrue(report.getMessages().isEmpty());

        report = validator.validate(factory.numberNode(0));
        assertTrue(report.isSuccess());
        assertTrue(report.getMessages().isEmpty());

        report = validator.validate(factory.numberNode(0.0));
        assertTrue(report.isSuccess());
        assertTrue(report.getMessages().isEmpty());

        report = validator.validate(factory.nullNode());
        assertTrue(report.isSuccess());
        assertTrue(report.getMessages().isEmpty());

        report = validator.validate(factory.textNode(""));
        assertTrue(report.isSuccess());
        assertTrue(report.getMessages().isEmpty());

        report = validator.validate(factory.objectNode());
        assertTrue(report.isSuccess());
        assertTrue(report.getMessages().isEmpty());
    }

    @Test
    public void testTypeAnyMatchesAll()
        throws JsonValidationFailureException
    {
        final ObjectNode schema = factory.objectNode();
        schema.put("type", "any");

        final JsonValidator validator = new JsonValidator(cfg, schema);

        ValidationReport report;

        report = validator.validate(factory.arrayNode());
        assertTrue(report.isSuccess());
        assertTrue(report.getMessages().isEmpty());

        report = validator.validate(factory.booleanNode(true));
        assertTrue(report.isSuccess());
        assertTrue(report.getMessages().isEmpty());

        report = validator.validate(factory.numberNode(0));
        assertTrue(report.isSuccess());
        assertTrue(report.getMessages().isEmpty());

        report = validator.validate(factory.numberNode(0.0));
        assertTrue(report.isSuccess());
        assertTrue(report.getMessages().isEmpty());

        report = validator.validate(factory.nullNode());
        assertTrue(report.isSuccess());
        assertTrue(report.getMessages().isEmpty());

        report = validator.validate(factory.textNode(""));
        assertTrue(report.isSuccess());
        assertTrue(report.getMessages().isEmpty());

        report = validator.validate(factory.objectNode());
        assertTrue(report.isSuccess());
        assertTrue(report.getMessages().isEmpty());
    }

    @Test
    public void testEmptyTypeArrayMatchesNothing()
        throws JsonValidationFailureException
    {
        final List<String> list = Arrays.asList("#: cannot match anything! "
            + "Empty simple type set _and_ I don't have any enclosed schema "
            + "either");

        final ObjectNode schema = factory.objectNode();
        schema.put("type", factory.arrayNode());

        final JsonValidator validator = new JsonValidator(cfg, schema);

        ValidationReport report;

        report = validator.validate(factory.arrayNode());
        assertFalse(report.isSuccess());
        assertEquals(report.getMessages(), list);

        report = validator.validate(factory.booleanNode(true));
        assertFalse(report.isSuccess());
        assertEquals(report.getMessages(), list);

        report = validator.validate(factory.numberNode(0));
        assertFalse(report.isSuccess());
        assertEquals(report.getMessages(), list);

        report = validator.validate(factory.numberNode(0.0));
        assertFalse(report.isSuccess());
        assertEquals(report.getMessages(), list);

        report = validator.validate(factory.nullNode());
        assertFalse(report.isSuccess());
        assertEquals(report.getMessages(), list);

        report = validator.validate(factory.textNode(""));
        assertFalse(report.isSuccess());
        assertEquals(report.getMessages(), list);

        report = validator.validate(factory.objectNode());
        assertFalse(report.isSuccess());
        assertEquals(report.getMessages(), list);
    }

    @Test
    public void testOneType()
        throws JsonValidationFailureException
    {
        testOne("one");
    }

    @Test
    public void testSimpleTypes()
        throws JsonValidationFailureException
    {
        testOne("simple");
    }

    @Test
    public void testUnionType()
        throws JsonValidationFailureException
    {
        testOne("union");
    }

    @Test
    public void testUnionOnly()
        throws JsonValidationFailureException
    {
        testOne("uniononly");
    }

    private void testOne(final String testName)
        throws JsonValidationFailureException
    {
        final JsonNode node = testNode.get(testName);
        final JsonNode schema = node.get("schema");
        final JsonNode good = node.get("good");
        final JsonNode bad = node.get("bad");

        final JsonValidator validator = new JsonValidator(cfg, schema);

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
