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

import eel.kitchen.jsonschema.JasonLoader;
import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import eel.kitchen.jsonschema.validators.type.IntegerValidator;
import org.codehaus.jackson.JsonNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class IntegerValidatorTest
{
    private JsonNode testNode, node;
    private IntegerValidator validator;
    private boolean ret;
    private List<String> messages;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        testNode = JasonLoader.load("integer.json");
    }

    @Test
    public void testMinimum()
        throws MalformedJasonSchemaException
    {
        node = testNode.get("minimum");

        validator = new IntegerValidator(node.get("schema"));
        validator.setup();
        ret = validator.validate(node.get("bad"));
        messages = validator.getValidationErrors();
        assertFalse(ret);
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "integer is strictly lower than the "
            + "required minimum");

        validator = new IntegerValidator(node.get("schema"));
        validator.setup();
        ret = validator.validate(node.get("good"));
        messages = validator.getValidationErrors();
        assertTrue(ret);
        assertEquals(messages.size(), 0);

    }

    @Test
    public void testExclusiveMinimum()
        throws MalformedJasonSchemaException
    {
        node = testNode.get("exclusiveMinimum");

        validator = new IntegerValidator(node.get("schema"));
        validator.setup();
        ret = validator.validate(node.get("bad"));
        messages = validator.getValidationErrors();
        assertFalse(ret);
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "integer equals to the minimum, " +
            "but should be strictly greater than it");

        validator = new IntegerValidator(node.get("schema"));
        validator.setup();
        ret = validator.validate(node.get("good"));
        messages = validator.getValidationErrors();
        assertTrue(ret);
        assertEquals(messages.size(), 0);
    }

    @Test
    public void testMaximum()
        throws MalformedJasonSchemaException
    {
        node = testNode.get("maximum");

        validator = new IntegerValidator(node.get("schema"));
        validator.setup();
        ret = validator.validate(node.get("bad"));
        messages = validator.getValidationErrors();
        assertFalse(ret);
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0),
            "integer is strictly greater than the " + "required maximum");

        validator = new IntegerValidator(node.get("schema"));
        validator.setup();
        ret = validator.validate(node.get("good"));
        messages = validator.getValidationErrors();
        assertTrue(ret);
        assertEquals(messages.size(), 0);
    }

    @Test
    public void testExclusiveMaximum()
        throws MalformedJasonSchemaException
    {
        node = testNode.get("exclusiveMaximum");

        validator = new IntegerValidator(node.get("schema"));
        validator.setup();
        ret = validator.validate(node.get("bad"));
        messages = validator.getValidationErrors();
        assertFalse(ret);
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "integer equals to the maximum, " +
            "but should be strictly lower than it");

        validator = new IntegerValidator(node.get("schema"));
        validator.setup();
        ret = validator.validate(node.get("good"));
        messages = validator.getValidationErrors();
        assertTrue(ret);
        assertEquals(messages.size(), 0);
    }

    @Test
    public void testDisivibleBy()
        throws MalformedJasonSchemaException
    {
        node = testNode.get("divisibleBy");

        validator = new IntegerValidator(node.get("schema"));
        validator.setup();
        ret = validator.validate(node.get("bad"));
        messages = validator.getValidationErrors();
        assertFalse(ret);
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "integer is not a multiple of the "
            + "declared divisor");

        validator = new IntegerValidator(node.get("schema"));
        validator.setup();
        ret = validator.validate(node.get("good"));
        messages = validator.getValidationErrors();
        assertTrue(ret);
        assertEquals(messages.size(), 0);
    }
}
