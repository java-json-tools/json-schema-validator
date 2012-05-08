/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.schema.JsonSchema;
import org.eel.kitchen.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

public final class DisallowTest
{
    private static final JsonNodeFactory factory = JsonNodeFactory.instance;

    private JsonNode testNode;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        testNode = JsonLoader.fromResource("/typekeywords/disallow.json");
    }

    @Test
    public void testEmptyDisallowArrayMatchesAll()
    {
        final ObjectNode node = factory.objectNode();
        node.put("disallow", factory.arrayNode());

        final JsonSchema schema = JsonSchema.fromNode(node);

        ValidationReport report;

        report = new ValidationReport();
        schema.validate(report, factory.arrayNode());
        assertTrue(report.isSuccess());


        report = new ValidationReport();
        schema.validate(report, factory.booleanNode(true));
        assertTrue(report.isSuccess());

        report = new ValidationReport();
        schema.validate(report, factory.numberNode(0));
        assertTrue(report.isSuccess());

        report = new ValidationReport();
        schema.validate(report, factory.numberNode(0.0));
        assertTrue(report.isSuccess());

        report = new ValidationReport();
        schema.validate(report, factory.nullNode());
        assertTrue(report.isSuccess());

        report = new ValidationReport();
        schema.validate(report, factory.textNode(""));
        assertTrue(report.isSuccess());

        report = new ValidationReport();
        schema.validate(report, factory.objectNode());
        assertTrue(report.isSuccess());
    }

    @Test
    public void testOneType()
    {
        testOne("one");
    }

    @Test
    public void testSimpleTypes()
    {
        testOne("simple");
    }

    @Test
    public void testUnionType()
    {
        testOne("union");
    }

    @Test
    public void testUnionOnly()
    {
        testOne("uniononly");
    }

    private void testOne(final String testName)
    {
        final JsonNode node = testNode.get(testName);
        final JsonNode schemaNode = node.get("schema");
        final JsonNode good = node.get("good");
        final JsonNode bad = node.get("bad");

        final JsonSchema schema = JsonSchema.fromNode(schemaNode);

        ValidationReport report = new ValidationReport();

        schema.validate(report, good);

        assertTrue(report.isSuccess());

        report = new ValidationReport();

        schema.validate(report, bad);

        assertFalse(report.isSuccess());
    }
}
