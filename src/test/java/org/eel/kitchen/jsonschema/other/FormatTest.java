/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.other;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.eel.kitchen.jsonschema.JsonValidator;
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.testng.Assert.*;

public final class FormatTest
{
    private static final JsonNodeFactory factory = JsonNodeFactory.instance;
    private JsonNode testNode;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        testNode = JsonLoader.fromResource("/other/format.json");
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
        final ObjectNode schema = factory.objectNode();
        schema.put("format", "host-name");

        final JsonValidator validator = new JsonValidator(schema);
        ValidationReport report;

        final String msg = "#: string is not a valid hostname";

        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i <= 257; i++)
            sb.append('a');

        report = validator.validate(factory.textNode(sb.toString()));

        assertFalse(report.isSuccess());

        assertEquals(msg, report.getMessages().get(0));

        report = validator.validate(factory.textNode("a+"));

        assertFalse(report.isSuccess());

        assertEquals(msg, report.getMessages().get(0));
    }

    private void testOne(final String fmt)
    {
        final JsonNode node = testNode.get(fmt);
        final JsonNode instance = node.get("instance");

        final ObjectNode schema = factory.objectNode();

        schema.put("format", fmt);

        final JsonValidator validator = new JsonValidator(schema);

        final ValidationReport report = validator.validate(instance);

        assertFalse(report.isSuccess());

        final List<String> messages = new LinkedList<String>();

        for (final JsonNode msg: node.get("messages"))
            messages.add(msg.getTextValue());

        assertEquals(report.getMessages(), messages);
    }
}
