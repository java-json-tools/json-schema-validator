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

package eel.kitchen.jsonschema;

import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class JasonSchemaTest
{
    private JsonNode node;
    private JasonSchema schema;
    private List<String> messages;

    @Test
    public void testDynDB()
        throws IOException
    {
        node = JasonHelper.load("fullschemas/dyndb.json");
        schema = new JasonSchema(node.get("schema"));

        assertFalse(schema.validate(node.get("ko")));
        messages = schema.getMessages();
        assertEquals(messages.size(), 2);
        assertEquals(messages.get(0), "#/table1: property id is required but "
            + "was not found");
        assertEquals(messages.get(1), "#/table2/croute/column: node is of "
            + "type boolean, expected [string]");

        assertTrue(schema.validate(node.get("ok")));
        messages = schema.getMessages();
        assertTrue(messages.isEmpty());
    }

    @Test
    public void test2()
        throws IOException
    {
        node = JasonHelper.load("fullschemas/test2.json");
        schema = new JasonSchema(node.get("schema"));

        assertFalse(schema.validate(node.get("ko")));
        messages = schema.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "#: additional properties were found "
            + "but schema forbids them");

        assertTrue(schema.validate(node.get("ok")));
        messages = schema.getMessages();
        assertTrue(messages.isEmpty());
    }

    @Test
    public void test3()
        throws IOException
    {
        node = JasonHelper.load("fullschemas/test3.json");
        schema = new JasonSchema(node.get("schema"));

        assertFalse(schema.validate(node.get("ko")));
        messages = schema.getMessages();
        assertEquals(messages.size(), 3);
        assertEquals(messages.get(0), "#/0: node is of type boolean, "
            + "expected [string]");
        assertEquals(messages.get(1), "#/1: property spirit depends on "
            + "elevated, but the latter was not found");
        assertEquals(messages.get(2), "#/2: node is of type string, "
            + "expected [integer]");

        assertTrue(schema.validate(node.get("ok")));
        messages = schema.getMessages();
        assertTrue(messages.isEmpty());
    }

    @Test
    public void test4()
        throws IOException
    {
        node = JasonHelper.load("fullschemas/test4.json");
        schema = new JasonSchema(node.get("schema"));

        assertFalse(schema.validate(node.get("ko")));
        messages = schema.getMessages();
        assertEquals(messages.size(), 3);
        assertEquals(messages.get(0), "#/0: node is of type boolean, "
            + "expected [integer, string]");
        assertEquals(messages.get(1), "#/1: integer is not a multiple of "
            + "the declared divisor");
        assertEquals(messages.get(2), "#/2: value is not a valid date");

        assertTrue(schema.validate(node.get("ok")));
        messages = schema.getMessages();
        assertTrue(messages.isEmpty());
    }
}
