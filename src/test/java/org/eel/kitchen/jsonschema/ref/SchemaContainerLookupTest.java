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
import org.eel.kitchen.util.JsonLoader;
import org.eel.kitchen.util.JsonPointer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

public final class SchemaContainerLookupTest
{
    private JsonNode schema;
    private SchemaContainer container;

    @BeforeClass
    public void setupSchema()
        throws IOException, JsonSchemaException
    {
        final JsonNode node = JsonLoader.fromResource("/schema-lookup.json");
        container = new SchemaContainer(node);
        schema = container.getSchema();
    }

    @Test
    public void lookingUpEmptyFragmentShouldReturnSchemaItself()
        throws JsonSchemaException
    {
        final JsonNode node = container.lookupFragment("");

        assertEquals(node, schema);
    }

    @Test
    public void lookingUpExistingPathReturnsCorrectResult()
        throws JsonSchemaException
    {
        // We have to choose one...
        final JsonNode expected = JsonNodeFactory.instance.objectNode();
        final JsonNode actual = container.lookupFragment("/default");

        assertEquals(actual, expected);
    }

    @Test
    public void lookingUpExistingIdReturnsCorrectResult()
        throws JsonSchemaException
    {
        final JsonPointer pointer = new JsonPointer("#/properties/properties");
        final JsonNode expected = pointer.getPath(schema);
        final JsonNode actual = container.lookupFragment("properties");

        assertEquals(actual, expected);
    }

    @Test
    public void lookingUpNonExistingPathOrIdThrowsException()
    {
        try {
            container.lookupFragment("/foo");
            fail("No exception thrown!");
        } catch (JsonSchemaException e) {
            assertEquals(e.getMessage(), "\"/foo\" does not match any "
                + "path/id in schema");
        }
    }
}
