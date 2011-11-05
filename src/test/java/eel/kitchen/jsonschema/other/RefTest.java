/**
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eel.kitchen.jsonschema.other;

import eel.kitchen.jsonschema.JsonValidator;
import eel.kitchen.jsonschema.ValidationReport;
import eel.kitchen.util.JsonLoader;
import eel.kitchen.util.RefResolver;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URL;

import static org.testng.Assert.*;

public final class RefTest
{
    private static final String SPEC = "http://json-schema.org/draft-03/schema";
    private JsonNode draftv3;


    @BeforeClass
    public void setUp()
        throws IOException
    {
        draftv3 = JsonLoader.fromURL(new URL(SPEC));
    }

    @Test
    public void testSchemaValidatesItself()
    {
        final JsonValidator validator = new JsonValidator(draftv3);

        final ValidationReport report = validator.validate(draftv3);

        assertTrue(report.isSuccess());
    }

    @Test
    public void testLoopingRefIsAnError()
    {
        final ObjectNode schemaNode = JsonNodeFactory.instance.objectNode();

        schemaNode.put("$ref", "#");

        final JsonValidator validator = new JsonValidator(schemaNode);

        final ValidationReport report
            = validator.validate(JsonNodeFactory.instance.arrayNode());

        assertFalse(report.isSuccess());

        assertEquals(report.getMessages().size(), 1);

        assertEquals(report.getMessages().get(0), "#: ref # points to "
            + "myself!");
    }

    @Test
    public void testRefResolver()
        throws IOException
    {
        final RefResolver resolver = new RefResolver(draftv3);

        assertEquals(resolver.resolve("#"), draftv3);

        assertTrue(resolver.resolve("#/foo").isMissingNode());

        assertEquals(resolver.resolve("#/properties/type/default"),
            JsonNodeFactory.instance.textNode("any"));
    }

}
