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

import eel.kitchen.jsonschema.JasonSchema;
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

public class MiscValidatorTest
{
    private JsonNode testNode, node;
    private JasonSchema schema;
    private boolean valid;
    private List<String> ret;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        testNode = JasonHelper.load("misc.json");
    }

    @Test
    public void testTwoBaseTypes()
    {
        node = testNode.get("twotypes");
        schema = new JasonSchema(node.get("schema"));

        valid = schema.validate(node.get("good"));
        assertTrue(valid);

        valid = schema.validate(node.get("bad"));
        assertFalse(valid);

        ret = schema.getMessages();
        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "$: node is of type integer, expected "
            + "[boolean, string]");
    }

    @Test
    public void testSimpleExclude()
    {
        node = testNode.get("simpleExclude");
        schema = new JasonSchema(node.get("schema"));

        valid = schema.validate(node.get("good"));
        assertTrue(valid);

        valid = schema.validate(node.get("bad"));
        assertFalse(valid);

        ret = schema.getMessages();
        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "$: node is of type boolean, expected "
            + "[array, integer, number, null, object, string]");
    }

    @Test
    public void testBoolean()
    {
        node = testNode.get("boolean");
        schema = new JasonSchema(node.get("schema"));

        valid = schema.validate(node.get("good"));
        assertTrue(valid);

        valid = schema.validate(node.get("bad"));
        assertFalse(valid);

        ret = schema.getMessages();
        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "$: node is of type null, "
            + "expected [boolean]");
    }

    @Test
    public void testNull()
    {
        node = testNode.get("null");
        schema = new JasonSchema(node.get("schema"));

        valid = schema.validate(node.get("good"));
        assertTrue(valid);

        valid = schema.validate(node.get("bad"));
        assertFalse(valid);

        ret = schema.getMessages();
        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "$: node is of type integer, "
            + "expected [null]");
    }

    @Test
    public void testEnum()
    {
        node = testNode.get("enum");
        schema = new JasonSchema(node.get("schema"));

        valid = schema.validate(node.get("good"));
        assertTrue(valid);

        valid = schema.validate(node.get("bad"));
        assertFalse(valid);

        ret = schema.getMessages();
        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "$: node does not match any value in the "
            + "enumeration");
    }
}
