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

import eel.kitchen.jsonschema.validators.type.ObjectValidator;
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class BrokenObjectSchemasTest
{
    private JsonNode schemas;
    private Validator v;
    private List<String> messages;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        schemas = JasonHelper.load("broken-object-schemas.json");
    }

    @Test
    public void testBrokenProperties()
    {
        v = new ObjectValidator().setSchema(schemas.get("broken-properties"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "properties is of type string, "
            + "expected [object]");
    }

    @Test
    public void testBrokenProperty()
    {
        v = new ObjectValidator().setSchema(schemas.get("broken-property"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "values of properties should be "
            + "objects");
    }

    @Test
    public void testBrokenRequired()
    {
        v = new ObjectValidator().setSchema(schemas.get("broken-required"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "required should be a boolean");
    }

    @Test
    public void testBrokenAdditional()
    {
        v = new ObjectValidator().setSchema(schemas.get("broken-additional"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "additionalProperties is of type "
            + "string, expected [boolean, object]");
    }

    @Test
    public void testBrokenDependencies()
    {
        v = new ObjectValidator().setSchema(schemas.get("broken-dependencies"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0),
            "dependencies is of type boolean, " + "expected [object]");
    }

    @Test
    public void testBrokenDependency()
    {
        v = new ObjectValidator().setSchema(schemas.get("broken-dependency"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "dependency value should be a string "
            + "or an array");
    }

    @Test
    public void testBrokenDependencyInArray()
    {
        v = new ObjectValidator()
            .setSchema(schemas.get("broken-dependency-element"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "dependency array elements should be "
            + "strings");
    }

    @Test
    public void testSelfDependency()
    {
        v = new ObjectValidator().setSchema(schemas.get("self-dependency"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "a property cannot depend on itself");
    }

    @Test
    public void testDuplicateDependency()
    {
        v = new ObjectValidator().setSchema(schemas.get("duplicate-dependency"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "duplicate entries in dependency array");
    }

    @Test
    public void testBrokenPatternProperties()
    {
        v = new ObjectValidator().setSchema(schemas.get("broken-patternprops"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "patternProperties is of type string, "
            + "expected [object]");
    }

    @Test
    public void testPatternPropertiesBrokenRegex()
    {
        v = new ObjectValidator()
            .setSchema(schemas.get("patternprops-brokenregex"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "invalid regex found in "
            + "patternProperties");
    }

    @Test
    public void testPatternPropertiesBrokenValue()
    {
        v = new ObjectValidator()
            .setSchema(schemas.get("patternprops-brokenvalue"));

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "values of patternProperties should be "
            + "objects");
    }
}
