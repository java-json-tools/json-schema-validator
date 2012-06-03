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

package org.eel.kitchen.jsonschema.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
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

    @Test
    public void schemaWithValidIDIsRegistered()
        throws JsonSchemaException
    {
        final String locator = "a://b.c#";
        final JsonNode node = factory.objectNode().put("id", locator);
        final URI uri = URI.create(locator);

        registry.register(node);
        assertNotNull(registry.get(uri));
    }

    @Test
    public void nullSchemaThrowsIAE()
        throws JsonSchemaException
    {
        try {
            registry.register(null);
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "schema is null");
        }
    }

    @Test
    public void cannotRegisterSameURITwice()
        throws JsonSchemaException
    {
        final String locator = "a://b.c#";
        final JsonNode node = factory.objectNode().put("id", locator);
        final URI uri = URI.create(locator);

        registry.register(node);
        try {
            registry.register(node);
            fail("No exception thrown!");
        } catch (JsonSchemaException e) {
            assertEquals(e.getMessage(), "URI \"" + uri + "\" is already "
                + "registered");
        }

    }
}
