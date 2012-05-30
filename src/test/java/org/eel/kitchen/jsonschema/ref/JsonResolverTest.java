/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
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

package org.eel.kitchen.jsonschema.ref;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.schema.SchemaNode;
import org.eel.kitchen.jsonschema.uri.URIManager;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class JsonResolverTest
{
    /*
     * Tests:
     *
     * - no ref returns equivalent document
     * - malformed ref returns equivalent document
     * - local ref never calls URIManager
     * - ref: # always loops
     * - local ref loop is detected
     * - cross schema loop is detected
     * - when travelling through s1 -> s2 -> s1, getting s1 is only called once
     */

    private URIManager manager;
    private final JsonNodeFactory factory = JsonNodeFactory.instance;

    @BeforeMethod
    public void initManager()
    {
        manager = mock(URIManager.class);
    }

    @Test
    public void noRefReturnsSelf()
        throws JsonSchemaException
    {
        final JsonNodeFactory factory = JsonNodeFactory.instance;
        final JsonNode node = factory.objectNode();
        final JsonResolver resolver = new JsonResolver(manager);

        final SchemaContainer container = new SchemaContainer(node);
        final SchemaNode schemaNode = new SchemaNode(container, node);

        final SchemaNode resolved = resolver.resolve(schemaNode);
        assertEquals(schemaNode, resolved);
    }

    @Test
    public void malformedRefReturnsSelf()
        throws JsonSchemaException
    {
        final JsonNode node = factory.objectNode().put("$ref", 1);
        final JsonResolver resolver = new JsonResolver(manager);

        final SchemaContainer container = new SchemaContainer(node);
        final SchemaNode schemaNode = new SchemaNode(container, node);

        final SchemaNode resolved = resolver.resolve(schemaNode);
        assertEquals(schemaNode, resolved);
    }

    @Test
    public void resolvingLocalRefSucceeds()
        throws JsonSchemaException
    {
        final JsonNode node = factory.objectNode()
            .put("$ref", "#/a")
            .put("a", "b");
        final JsonResolver resolver = new JsonResolver(manager);

        final SchemaContainer container = new SchemaContainer(node);
        final SchemaNode schemaNode = new SchemaNode(container, node);

        resolver.resolve(schemaNode);
        verify(manager, never()).getContent(any(URI.class));
    }

    @Test
    public void resolvingIndirectLocalRefSucceeds()
        throws JsonSchemaException
    {
        final JsonNode refB = factory.objectNode().put("$ref", "#/b");
        /*
         * node is:
         * {
         *     "$ref": "#/a",
         *     "a": {
         *         "$ref": "#/b"
         *     },
         *     "b": ""
         * }
         *
         * result should be: ""
         */
        final ObjectNode node = factory.objectNode()
            .put("$ref", "#/a")
            .put("b", "");

        // Watch out... Unlike .put() with a second parameter other than
        // JsonNode, this version of .put() returns the previous value!
        node.put("a", refB);

        final JsonResolver resolver = new JsonResolver(manager);

        final SchemaContainer container = new SchemaContainer(node);
        final SchemaNode schemaNode = new SchemaNode(container, node);
        final SchemaNode expected = new SchemaNode(container,
            factory.textNode(""));

        final SchemaNode result = resolver.resolve(schemaNode);
        assertEquals(result, expected);
    }

    @Test(timeOut = 5000)
    public void LocalRefLoopIsDetected()
        throws JsonSchemaException
    {
        final JsonNode node = factory.objectNode()
            .put("$ref", "#");

        final SchemaContainer container = new SchemaContainer(node);
        final SchemaNode schemaNode = new SchemaNode(container, node);

        final JsonResolver resolver = new JsonResolver(manager);

        try {
            resolver.resolve(schemaNode);
            fail("No excpetion thrown!");
        } catch (JsonSchemaException e) {
            assertEquals(e.getMessage(), "ref loop detected");
        }
    }
}
