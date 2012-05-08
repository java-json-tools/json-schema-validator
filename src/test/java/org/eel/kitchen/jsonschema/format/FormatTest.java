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

package org.eel.kitchen.jsonschema.format;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eel.kitchen.jsonschema.schema.JsonSchema;
import org.eel.kitchen.jsonschema.schema.ValidationReport;
import org.eel.kitchen.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

public final class FormatTest
{
    private static final JsonNodeFactory factory = JsonNodeFactory.instance;
    private JsonNode testNode;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        testNode = JsonLoader.fromResource("/format.json");
    }

    @Test
    public void testStyle()
    {
        testOne("style");
    }

    @Test
    public void testIPV4()
    {
        testOne("ip-address");
    }

    @Test
    public void testPhone()
    {
        testOne("phone");
    }

    @Test
    public void testUnixEpoch()
    {
        testOne("utc-millisec");
    }

    @Test
    public void testURI()
    {
        testOne("uri");
    }

    @Test
    public void testDate()
    {
        testOne("date");
    }

    @Test
    public void testDateTime()
    {
        testOne("date-time");
    }

    @Test
    public void testTime()
    {
        testOne("time");
    }

    @Test
    public void testHostName()
    {
        final ObjectNode schemaNode = factory.objectNode();
        schemaNode.put("format", "host-name");

        final JsonSchema schema = JsonSchema.fromNode(schemaNode);

        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i <= 257; i++)
            sb.append('a');

        ValidationReport report;

        report = new ValidationReport();
        schema.validate(report, factory.textNode(sb.toString()));
        assertFalse(report.isSuccess());

        report = new ValidationReport();
        schema.validate(report, factory.textNode("a+"));
        assertFalse(report.isSuccess());
    }

    @Test
    public void testUnknownFormatIsIgnored()
    {
        final ObjectNode schemaNode = factory.objectNode();
        schemaNode.put("format", "izjefoizjoeijf");

        final JsonSchema schema = JsonSchema.fromNode(schemaNode);

        final ValidationReport report = new ValidationReport();

        schema.validate(report, factory.nullNode());
        assertTrue(report.isSuccess());
    }

    private void testOne(final String fmt)
    {
        final JsonNode node = testNode.get(fmt);
        final JsonNode instance = node.get("instance");

        final ObjectNode schemaNode = factory.objectNode();

        schemaNode.put("format", fmt);

        final JsonSchema schema = JsonSchema.fromNode(schemaNode);

        final ValidationReport report = new ValidationReport();
        schema.validate(report, instance);
        assertFalse(report.isSuccess());
    }
}
