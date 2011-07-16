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

package eel.kitchen.jsonschema.validators.misc;

import eel.kitchen.jsonschema.validators.Validator;
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public final class DependenciesValidatorTest
{
    private static final List<String> ref = Arrays.asList(
        "property p1 depends on p3, but the latter was not found",
        "property p1 depends on p4, but the latter was not found"
    );

    private JsonNode brokenSchemas, schema;
    private JsonNode testNode;
    private final Validator v = new DependenciesValidator();
    private List<String> messages;

    @BeforeClass
    public void setup()
        throws IOException
    {
        testNode = JasonHelper.load("dependencies.json");
        brokenSchemas = testNode.get("broken");
    }

    @Test(priority = 1)
    public void testDependencyValidator()
    {
        schema = testNode.get("schema");
        v.setSchema(schema);

        assertTrue(v.setup());
        assertTrue(v.getMessages().isEmpty());

        assertTrue(v.validate(testNode.get("good")));
        assertTrue(v.getMessages().isEmpty());

        assertFalse(v.validate(testNode.get("bad")));
        assertEquals(v.getMessages().size(), 2);
        assertTrue(v.getMessages().containsAll(ref));
    }

    @Test
    public void testInvalidType()
    {
        schema = brokenSchemas.get("invalid-type");
        v.setSchema(schema);

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "dependencies is of type null, "
            + "expected [object]");
    }

    @Test
    public void testInvalidValue()
    {
        schema = brokenSchemas.get("invalid-value");
        v.setSchema(schema);

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0),
            "dependency value should be a string " + "or an array");
    }

    @Test
    public void testInvalidElement()
    {
        schema = brokenSchemas.get("invalid-element");
        v.setSchema(schema);

        assertFalse(v.setup());

        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "dependency array elements should be "
            + "strings");
    }
}
