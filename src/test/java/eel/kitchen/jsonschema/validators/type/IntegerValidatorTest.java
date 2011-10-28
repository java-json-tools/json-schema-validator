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

import static org.testng.Assert.*;

public class IntegerValidatorTest
{
    private JsonNode testNode, node;
    private final Validator v = new IntegerValidator();
    private boolean ret;
    private List<String> messages;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        testNode = JasonHelper.load("integer.json");
    }

    @Test
    public void testMinimum()
    {
        node = testNode.get("minimum");
        v.setSchema(node.get("schema"));

        ret = v.setup();

        assertTrue(ret);

        ret = v.validate(node.get("bad"));
        messages = v.getMessages();

        assertFalse(ret);
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "integer is strictly lower than the "
            + "required minimum");

        ret = v.validate(node.get("good"));
        messages = v.getMessages();

        assertTrue(ret);
        assertEquals(messages.size(), 0);

    }

    @Test
    public void testExclusiveMinimum()
    {
        node = testNode.get("exclusiveMinimum");
        v.setSchema(node.get("schema"));

        ret = v.setup();

        assertTrue(ret);

        ret = v.validate(node.get("bad"));
        messages = v.getMessages();

        assertFalse(ret);
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "integer equals to the minimum, " +
            "but should be strictly greater than it");

        ret = v.validate(node.get("good"));
        messages = v.getMessages();

        assertTrue(ret);
        assertEquals(messages.size(), 0);
    }

    @Test
    public void testMaximum()
    {
        node = testNode.get("maximum");
        v.setSchema(node.get("schema"));

        ret = v.setup();

        assertTrue(ret);

        ret = v.validate(node.get("bad"));
        messages = v.getMessages();

        assertFalse(ret);
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0),
            "integer is strictly greater than the " + "required maximum");

        ret = v.validate(node.get("good"));
        messages = v.getMessages();

        assertTrue(ret);
        assertEquals(messages.size(), 0);
    }

    @Test
    public void testExclusiveMaximum()
    {
        node = testNode.get("exclusiveMaximum");
        v.setSchema(node.get("schema"));

        ret = v.setup();

        assertTrue(ret);

        ret = v.validate(node.get("bad"));
        messages = v.getMessages();

        assertFalse(ret);
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "integer equals to the maximum, " +
            "but should be strictly lower than it");

        ret = v.validate(node.get("good"));
        messages = v.getMessages();

        assertTrue(ret);
        assertEquals(messages.size(), 0);
    }

    @Test
    public void testDisivibleBy()
    {
        node = testNode.get("divisibleBy");
        v.setSchema(node.get("schema"));

        ret = v.setup();

        assertTrue(ret);

        ret = v.validate(node.get("bad"));
        messages = v.getMessages();
        assertFalse(ret);
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "integer is not a multiple of the "
            + "declared divisor");

        ret = v.validate(node.get("good"));
        messages = v.getMessages();
        assertTrue(ret);
        assertEquals(messages.size(), 0);
    }
}
