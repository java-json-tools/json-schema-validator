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

import eel.kitchen.jsonschema.validators.type.ArrayValidator;
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.*;

public class BrokenArraySchemasTest
{
    private JsonNode schemas;
    private final Validator v = new ArrayValidator();
    private List<String> messages;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        schemas = JasonHelper.load("broken-array-schemas.json");
    }

    @Test
    public void testBrokenMinItems()
    {
        v.setSchema(schemas.get("broken-minItems"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "minItems is of type boolean, "
            + "expected [integer]");
    }

    @Test
    public void testBrokenMaxItems()
    {
        v.setSchema(schemas.get("broken-maxItems"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "maxItems is of type string, "
            + "expected [integer]");
    }

    @Test
    public void testNegativeMinItems()
    {
        v.setSchema(schemas.get("negative-minItems"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "minItems is negative");
    }

    @Test
    public void testMinItemsOverflow()
    {
        v.setSchema(schemas.get("minItems-overflow"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "minItems overflow");
    }

    @Test
    public void testNegativeMaxItems()
    {
        v.setSchema(schemas.get("negative-maxItems"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "maxItems is negative");
    }

    @Test
    public void testMaxItemsOverflow()
    {
        v.setSchema(schemas.get("maxItems-overflow"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "maxItems overflow");
    }

    @Test
    public void testInvertedMinMax()
    {
        v.setSchema(schemas.get("inverted-minmax"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "minItems is greater than maxItems");
    }

    @Test
    public void testBrokenUniqueItems()
    {
        v.setSchema(schemas.get("broken-uniqueItems"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "uniqueItems is of type string, "
            + "expected [boolean]");
    }

    @Test
    public void testBrokenItems()
    {
        v.setSchema(schemas.get("broken-items"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "items is of type string, "
            + "expected [array, object]");
    }

    @Test
    public void testBrokenItemsValue()
    {
        v.setSchema(schemas.get("broken-items-value"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0),
            "members of the items array should be " + "objects");
    }

    @Test
    public void testBrokenAdditionalItems()
    {
        v.setSchema(schemas.get("broken-additionalItems"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "additionalItems is of type string, "
            + "expected [boolean, object]");
    }

    @Test
    public void testIncoherentMinItems()
    {
        v.setSchema(schemas.get("incoherent-minItems"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "minItems is greater than what the "
            + "schema allows (tuples, additional)");
    }

    @Test
    public void testNoTuplesAdditional()
    {
        v.setSchema(schemas.get("notuples-additional"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "additionalItems is an object but "
            + "tuple validation is not in effect");
    }
}
