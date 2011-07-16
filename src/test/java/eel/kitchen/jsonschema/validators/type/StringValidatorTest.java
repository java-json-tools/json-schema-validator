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

public class StringValidatorTest
{
    private JsonNode testNode, node;
    private final Validator v = new StringValidator();
    private boolean ret;
    private List<String> messages;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        testNode = JasonHelper.load("string.json");
    }

    @Test
    public void testMinLength()
    {
        node = testNode.get("minLength");
        v.setSchema(node.get("schema"));

        ret = v.setup();

        assertTrue(ret);

        ret = v.validate(node.get("bad"));
        messages = v.getMessages();

        assertFalse(ret);
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "string length is less than the required " +
            "minimum");

        ret = v.validate(node.get("good"));
        messages = v.getMessages();

        assertTrue(ret);
        assertEquals(messages.size(), 0);
    }

    @Test
    public void testMaxLength()
    {
        node = testNode.get("maxLength");
        v.setSchema(node.get("schema"));

        ret = v.setup();

        assertTrue(ret);

        ret = v.validate(node.get("bad"));
        messages = v.getMessages();

        assertFalse(ret);
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "string length exceeds the required maximum");

        ret = v.validate(node.get("good"));
        messages = v.getMessages();

        assertTrue(ret);
        assertEquals(messages.size(), 0);
    }

    @Test
    public void testPattern()
    {
        node = testNode.get("pattern");
        v.setSchema(node.get("schema"));

        ret = v.setup();

        assertTrue(ret);

        ret = v.validate(node.get("bad"));
        messages = v.getMessages();

        assertFalse(ret);
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "string does not match regular expression");

        ret = v.validate(node.get("good"));
        messages = v.getMessages();

        assertTrue(ret);
        assertEquals(messages.size(), 0);
    }
}
