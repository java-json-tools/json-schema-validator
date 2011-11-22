/*
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
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.other;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.JsonValidator;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.UUID;

import static org.testng.Assert.*;

public final class RefTest
{
    private JsonNode torture;
    private static final JsonNodeFactory factory = JsonNodeFactory.instance;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        torture = JsonLoader.fromResource("/ref/torture.json");
    }

    @Test
    public void testRefNotAlone()
        throws JsonValidationFailureException
    {
        final ObjectNode schemaNode = factory.objectNode();

        schemaNode.put("$ref", "#");
        schemaNode.put("format", "uri");

        final JsonValidator validator = new JsonValidator(schemaNode);

        final ValidationReport report = validator.validate(factory.arrayNode());

        assertFalse(report.isSuccess());

        assertEquals(report.getMessages().size(), 1);

        assertEquals(report.getMessages().get(0),  "# [schema:$ref]: $ref "
            + "can only be by itself, or paired with required");
    }

    @Test
    public void testLoopingRef()
        throws JsonValidationFailureException
    {
        final ObjectNode schemaNode = factory.objectNode();

        schemaNode.put("$ref", "#");

        final JsonValidator validator = new JsonValidator(schemaNode);

        final ValidationReport report = validator.validate(factory.arrayNode());

        assertFalse(report.isSuccess());

        assertEquals(report.getMessages().size(), 1);

        assertEquals(report.getMessages().get(0),  "#: FATAL: schema "
            + "{\"$ref\":\"#\"} loops on itself");
    }

    @Test
    public void testMissingPath()
        throws JsonValidationFailureException
    {
        final JsonNode schema = torture.get("missingref");

        final JsonValidator validator = new JsonValidator(schema);

        final ValidationReport report = validator.validate(factory.nullNode());

        assertTrue(report.isError());

        assertEquals(1, report.getMessages().size());

        assertEquals(report.getMessages().get(0),  "#: FATAL: no match in "
            + "schema for path #/nope");
    }

    @Test
    public void testDisallowLoopRef()
        throws JsonValidationFailureException
    {
        final JsonNode schema = torture.get("disallow");

        final JsonValidator validator = new JsonValidator(schema);

        final ValidationReport report = validator.validate(factory.nullNode());

        assertTrue(report.isError());

        assertEquals(1, report.getMessages().size());

        assertEquals("#: FATAL: schema "
            + "{\"disallow\":[{\"$ref\":\"#\"}]} loops on itself",
            report.getMessages().get(0));
    }

    @Test
    public void testUnsupportedScheme()
        throws JsonValidationFailureException
    {
        final JsonNode schema = torture.get("unsupportedScheme");

        final JsonValidator validator = new JsonValidator(schema);

        final ValidationReport report = validator.validate(factory.nullNode());

        assertTrue(report.isError());

        assertEquals(1, report.getMessages().size());

        assertEquals("#: FATAL: cannot use ref ftp://some.site/some/schema:"
            + " unsupported scheme ftp", report.getMessages().get(0));
    }

    @Test
    public void testNonEmptySSP()
        throws JsonValidationFailureException
    {
        final JsonNode schema = torture.get("nonEmptySSP");

        final JsonValidator validator = new JsonValidator(schema);

        final ValidationReport report = validator.validate(factory.nullNode());

        assertTrue(report.isError());

        assertEquals(1, report.getMessages().size());

        assertEquals("#: FATAL: cannot use ref a/b/c#/d/e: invalid URI: "
            + "URI is not absolute and is not a JSON Pointer either",
            report.getMessages().get(0));
    }

    @Test
    public void testUnknownHost()
        throws URISyntaxException, JsonValidationFailureException
    {
        String hostname;

        /*
         * That is one good way of finding a non existent hostname... And
         * while there is a distant possibility that between this point and
         * the actual test, the hostname becomes valid,
         * the chance is pretty slim...
         *
         * TODO: maybe use mockito for that? But at what level?
         */
        while (true) {
            hostname = UUID.randomUUID().toString();
            try {
                InetAddress.getByName(hostname);
            } catch (UnknownHostException ignored) {
                break;
            }
        }

        final URI uri = new URI("http", hostname, null, null);

        final String ref = uri.toASCIIString();

        final String errmsg = String.format("#: FATAL: cannot download schema"
            + " at ref %s: java.net.UnknownHostException: %s", ref, hostname);

        final ObjectNode schema = factory.objectNode();
        schema.put("$ref", ref);

        final JsonValidator validator = new JsonValidator(schema);

        final ValidationReport report = validator.validate(factory.nullNode());

        assertTrue(report.isError());

        assertEquals(report.getMessages().size(), 1);
        assertEquals(report.getMessages().get(0), errmsg);
    }

    @Test
    public void testCrossSchemaLoop()
        throws JsonValidationFailureException
    {
        final ObjectNode schema1 = factory.objectNode();
        schema1.put("$ref", "#/schema2");

        final ObjectNode schema2 = factory.objectNode();
        schema2.put("$ref", "#/schema1");

        final ObjectNode schema = factory.objectNode();
        schema.put("schema1", schema1);
        schema.put("schema2", schema2);

        final JsonValidator validator = new JsonValidator(schema);

        final ValidationReport report
            = validator.validate("#/schema1", factory.nullNode());

        assertTrue(report.isError());

        assertEquals(report.getMessages().size(), 1);
        assertEquals(report.getMessages().get(0), "#: FATAL: schema {\"$ref\":"
            + "\"#/schema2\"} loops on itself");
    }
}
