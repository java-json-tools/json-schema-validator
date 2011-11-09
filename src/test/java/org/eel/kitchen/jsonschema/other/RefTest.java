/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.eel.kitchen.jsonschema.other;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.eel.kitchen.jsonschema.JsonValidator;
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URL;

import static org.testng.Assert.*;

public final class RefTest
{
    private static final String SPEC = "http://json-schema.org/draft-03/schema";
    private JsonNode draftv3;
    private JsonNode torture;


    @BeforeClass
    public void setUp()
        throws IOException
    {
        draftv3 = JsonLoader.fromURL(new URL(SPEC));
        torture = JsonLoader.fromResource("/ref/torture.json");
    }

    @Test
    public void testSchemaValidatesItself()
    {
        final JsonValidator validator = new JsonValidator(draftv3);

        final ValidationReport report = validator.validate(draftv3);

        assertTrue(report.isSuccess());
    }

    //TODO: test with a depth more than 1. I know it works, but still
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

        assertEquals(report.getMessages().get(0),  "#: FATAL: schema "
            + "{\"$ref\":\"#\"} loops on itself");
    }

    @Test
    public void testMissingPathIsFatal()
    {
        final JsonNode schema = torture.get("missingref");

        final JsonValidator validator = new JsonValidator(schema);

        final ValidationReport report = validator.validate(JsonNodeFactory
            .instance.nullNode());

        assertTrue(report.isError());

        assertEquals(1, report.getMessages().size());

        assertEquals(report.getMessages().get(0),  "#: FATAL: no match in "
            + "schema for path #/nope");
    }

    @Test
    public void testDisallowLoopRefIsFatal()
    {
        final JsonNode schema = torture.get("disallow");

        final JsonValidator validator = new JsonValidator(schema);

        final ValidationReport report = validator.validate(JsonNodeFactory
            .instance.nullNode());

        assertTrue(report.isError());

        assertEquals(1, report.getMessages().size());

        assertEquals("#: FATAL: schema "
            + "{\"disallow\":[{\"$ref\":\"#\"}]} loops on itself",
            report.getMessages().get(0));
    }

    @Test
    public void testUnsupportedSchemeIsFatal()
    {
        final JsonNode schema = torture.get("unsupportedScheme");

        final JsonValidator validator = new JsonValidator(schema);

        final ValidationReport report = validator.validate(JsonNodeFactory
            .instance.nullNode());

        assertTrue(report.isError());

        assertEquals(1, report.getMessages().size());

        assertEquals("#: FATAL: cannot use ref ftp://some.site/some/schema,"
            + " only HTTP is supported currently", report.getMessages().get(0));
    }

    @Test
    public void testNonEmptySSPIsFatal()
    {
        final JsonNode schema = torture.get("nonEmptySSP");

        final JsonValidator validator = new JsonValidator(schema);

        final ValidationReport report = validator.validate(JsonNodeFactory
            .instance.nullNode());

        assertTrue(report.isError());

        assertEquals(1, report.getMessages().size());

        assertEquals("#: FATAL: invalid URI a/b/c#/d/e: non absolute URI"
            + " but non empty scheme specific part",
            report.getMessages().get(0));
    }
}
