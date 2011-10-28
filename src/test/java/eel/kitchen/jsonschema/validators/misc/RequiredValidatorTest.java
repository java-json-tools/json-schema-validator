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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

public final class RequiredValidatorTest
{
    private JsonNode testNode;
    private final Validator v = new RequiredValidator();
    private final List<String> reference = Arrays.asList(
        "property p3 is required but was not found",
        "property p4 is required but was not found"
    );
    private List<String> messages;

    @BeforeClass
    public void setup()
        throws IOException
    {
        testNode = JasonHelper.load("required.json");
    }

    @Test(priority = 0)
    public void testBroken()
    {
        v.setSchema(testNode.get("broken"));

        assertFalse(v.setup());
        messages = v.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "required should be a boolean");
    }

    @Test(priority = 1)
    public void testValidation()
    {
        v.setSchema(testNode.get("schema"));

        assertTrue(v.setup());

        assertTrue(v.validate(testNode.get("good")));

        assertFalse(v.validate(testNode.get("bad")));
        messages = new ArrayList<String>(v.getMessages());
        assertEquals(messages.size(), 2);
        messages.removeAll(reference);
        assertTrue(messages.isEmpty());
    }
}
