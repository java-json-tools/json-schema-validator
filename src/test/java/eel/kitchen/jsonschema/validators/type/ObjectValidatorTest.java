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

import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
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

public class ObjectValidatorTest
{
    private JsonNode testNode, node;
    private Validator validator;
    private boolean ret;
    private List<String> messages;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        testNode = JasonHelper.load("object.json");
    }

    @Test
    public void testRequired()
        throws MalformedJasonSchemaException
    {
        node = testNode.get("required");

        validator = new ObjectValidator().setSchema(node.get("schema"));
        validator.setup();

        ret = validator.validate(node.get("bad"));
        messages = validator.getMessages();
        assertFalse(ret);
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "property p1 is required but was not found");

        ret = validator.validate(node.get("good"));
        messages = validator.getMessages();
        assertTrue(ret);
        assertTrue(messages.isEmpty());
    }

    @Test
    public void testNoAdditional()
        throws MalformedJasonSchemaException
    {
        node = testNode.get("noAdditional");

        validator = new ObjectValidator().setSchema(node.get("schema"));
        validator.setup();

        ret = validator.validate(node.get("bad"));
        messages = validator.getMessages();
        assertFalse(ret);
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "additional properties were found but " +
            "schema forbids them");

        ret = validator.validate(node.get("good"));
        messages = validator.getMessages();
        assertTrue(ret);
        assertTrue(messages.isEmpty());
    }

    @Test
    public void testDependencies()
        throws MalformedJasonSchemaException
    {
        node = testNode.get("dependencies");

        validator = new ObjectValidator().setSchema(node.get("schema"));
        validator.setup();

        ret = validator.validate(node.get("bad"));
        messages = validator.getMessages();
        assertFalse(ret);
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "property p1 depends on p3, "
            + "but the latter was not found");

        ret = validator.validate(node.get("good"));
        messages = validator.getMessages();
        assertTrue(ret);
        assertTrue(messages.isEmpty());
    }
}
