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

package eel.kitchen.jsonschema.validators;

import eel.kitchen.jsonschema.validators.type.StringValidator;
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.*;

public class BrokenStringSchemasTest
{
    private JsonNode schemas;
    private final Validator v = new StringValidator();
    private List<String> messages;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        schemas = JasonHelper.load("broken-string-schemas.json");
    }

    @Test
    public void testBrokenMinLength()
    {
        v.setSchema(schemas.get("broken-minLength"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "minLength is of type number, "
            + "expected [integer]");
    }

    @Test
    public void testNegativeMinLength()
    {
        v.setSchema(schemas.get("negative-minLength"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "minLength is lower than 0");
    }

    @Test
    public void testMinLengthOverflow()
    {
        v.setSchema(schemas.get("minLength-overflow"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "minLength overflow");
    }

    @Test
    public void testBrokenMaxLength()
    {
        v.setSchema(schemas.get("broken-maxLength"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "maxLength is of type string, "
            + "expected [integer]");
    }

    @Test
    public void testNegativeMaxLength()
    {
        v.setSchema(schemas.get("negative-maxLength"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "maxLength is lower than 0");
    }

    @Test
    public void testMaxLengthOverflow()
    {
        v.setSchema(schemas.get("maxLength-overflow"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "maxLength overflow");
    }

    @Test
    public void testInvertedMinMax()
    {
        v.setSchema(schemas.get("inverted-minmax"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "minLength is greater than maxLength");
    }

    @Test
    public void testBrokenPatternType()
    {
        v.setSchema(schemas.get("broken-pattern-type"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "pattern is of type boolean, "
            + "expected [string]");
    }

    @Test
    public void testIllegalPattern()
    {
        v.setSchema(schemas.get("illegal-pattern"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "pattern is an invalid regular "
            + "expression");
    }
}
