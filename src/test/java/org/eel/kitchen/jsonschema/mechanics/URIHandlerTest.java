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

package org.eel.kitchen.jsonschema.mechanics;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.JsonValidator;
import org.eel.kitchen.jsonschema.main.ValidationConfig;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.uri.HTTPURIHandler;
import org.eel.kitchen.jsonschema.uri.URIHandler;
import org.eel.kitchen.util.JsonLoader;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;

import static org.testng.Assert.*;

public final class URIHandlerTest
{
    private static final JsonNode schema
        = JsonNodeFactory.instance.objectNode();

    private static final JsonValidator validator;

    private static final ValidationConfig cfg = new ValidationConfig();

    static {
        try {
            validator = new JsonValidator(cfg, schema);
        } catch (JsonValidationFailureException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static final URIHandler handler = new HTTPURIHandler();

    @Test
    public void testRegisteringExistingSchemeFails()
    {
        try {
            validator.registerURIHandler("http", handler);
            fail("No exception thrown");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "scheme http already registered");
        }
    }

    @Test
    public void testNullSchemeFails()
    {
        try {
            validator.registerURIHandler(null, handler);
            fail("No exception thrown");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "scheme is null");
        }
    }

    @Test
    public void testNullHandlerFails()
    {
        try {
            validator.registerURIHandler("myscheme", null);
            fail("No exception thrown");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "handler is null");
        }
    }

    @Test
    public void testUnregisteringNullSchemeFails()
    {
        try {
            validator.unregisterURIHandler(null);
            fail("No exception thrown");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "scheme is null");
        }
    }

    @Test
    public void testInvalidSchemeFails()
    {
        try {
            validator.registerURIHandler("+23", handler);
            fail("No exception thrown");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "invalid scheme +23");
        }
    }

    @Test
    public void testUnregisteringAndRegistering()
    {
        validator.unregisterURIHandler("http");
        validator.registerURIHandler("http", handler);
        assertTrue(true);
    }

    @Test
    public void aspirinTime()
        throws IOException, JsonValidationFailureException
    {
        final JsonNode testNode
            = JsonLoader.fromResource("/ref/torture.json").get("aspirin");

        final URIHandler handler = new URIHandler()
        {
            @Override
            public JsonNode getDocument(final URI uri)
                throws IOException
            {
                return testNode;
            }
        };

        final JsonValidator validator = new JsonValidator(cfg, testNode);

        validator.registerURIHandler("mystuff", handler);

        final ValidationReport report = validator.validate("#/link1",
            JsonNodeFactory.instance.nullNode());

        assertFalse(report.isSuccess());
        assertTrue(report.isError());

        assertEquals(report.getMessages().size(), 1);
        assertEquals(report.getMessages().get(0), "#: FATAL: schema "
            + "{\"$ref\":\"mystuff:a#/link2\"} loops on itself");
    }
}
