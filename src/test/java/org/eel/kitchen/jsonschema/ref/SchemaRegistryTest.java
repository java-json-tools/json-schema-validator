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
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.*;

public final class SchemaRegistryTest
{
    private SchemaRegistry registry;
    private final JsonNodeFactory factory = JsonNodeFactory.instance;

    @BeforeMethod
    public void setRegistry()
    {
        registry = new SchemaRegistry();
    }

    /*
     * Note that we test here with whatever URI we want. But in reality,
     * only absolute URIs will ever find their way into the registry,
     * this is guaranteed by the callers.
     */
    @Test
    public void testRegisteredSchemaShowsUp()
        throws JsonSchemaException
    {
        final JsonNode node = factory.objectNode();
        final SchemaContainer container = new SchemaContainer(node);
        final URI uri = URI.create("");

        registry.register(uri, container);

        assertSame(registry.get(uri), container);
    }

    @Test
    public void cannotRegisterNullContainer()
    {
        final URI uri = URI.create("");

        try {
            registry.register(uri, null);
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "container is null");
        }
    }

    @Test
    public void cannotRegisterNullURI()
        throws JsonSchemaException
    {
        final JsonNode node = factory.objectNode();
        final SchemaContainer container = new SchemaContainer(node);

        try {
            registry.register(null, container);
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "uri is null");
        }
    }
}
