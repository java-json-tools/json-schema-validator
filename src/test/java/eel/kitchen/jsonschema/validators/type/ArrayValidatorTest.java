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

package eel.kitchen.jsonschema.validators.type;

import eel.kitchen.jsonschema.validators.Validator;
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ArrayValidatorTest
{
    private JsonNode testNode, node;
    private final Validator v = new ArrayValidator();
    private List<String> messages;
    private boolean ret;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        testNode = JasonHelper.load("array.json");
    }

    @Test
    public void testMinItems()
    {
        node = testNode.get("minItems");
        v.setSchema(node.get("schema"));

        assertTrue(v.setup());

        ret = v.validate(node.get("bad"));
        messages = v.getMessages();

        assertFalse(ret);
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "array has less than minItems elements");

        ret = v.validate(node.get("good"));
        messages = v.getMessages();

        assertTrue(ret);
        assertTrue(messages.isEmpty());
    }

    @Test
    public void testMaxItems()
    {
        node = testNode.get("maxItems");
        v.setSchema(node.get("schema"));

        assertTrue(v.setup());

        ret = v.validate(node.get("bad"));
        messages = v.getMessages();

        assertFalse(ret);
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "array has more than maxItems elements");

        ret = v.validate(node.get("good"));
        messages = v.getMessages();

        assertTrue(ret);
        assertTrue(messages.isEmpty());
    }

    @Test
    public void testUniqueItems()
    {
        node = testNode.get("uniqueItems");
        v.setSchema(node.get("schema"));

        assertTrue(v.setup());

        ret = v.validate(node.get("bad"));
        messages = v.getMessages();

        assertFalse(ret);
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "items in the array are not unique");

        ret = v.validate(node.get("good"));
        messages = v.getMessages();

        assertTrue(ret);
        assertTrue(messages.isEmpty());
    }

    @Test
    public void testItemsTuples()
    {
        node = testNode.get("itemsTuples");
        v.setSchema(node.get("schema"));

        assertTrue(v.setup());

        ret = v.validate(node.get("bad"));
        messages = v.getMessages();

        assertFalse(ret);
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "array has extra elements, "
            + "which the schema disallows");

        ret = v.validate(node.get("good"));
        messages = v.getMessages();

        assertTrue(ret);
        assertTrue(messages.isEmpty());
    }
}
