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

package org.eel.kitchen.jsonschema.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

public final class JsonReferenceTest
{
    private JsonNode testData;
    private final JsonNodeFactory factory = JsonNodeFactory.instance;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        testData = JsonLoader.fromResource("/jsonref/testdata.json");
    }

    @Test
    public void testSimpleRefLoop()
        throws IOException
    {
        final ObjectNode node = factory.objectNode();
        node.put("$ref", "#");

        try {
            JsonReference.resolveRef(node, node);
        } catch (JsonSchemaException e) {
            assertEquals(e.getMessage(), "ref \"#\" loops on itself");
        }
    }

    @Test
    public void testMalformedSchemaLocator()
        throws IOException
    {
        final ObjectNode node = factory.objectNode();
        node.put("id", "a");
        node.put("$ref", "#whatever");

        try {
            JsonReference.resolveRef(node, node);
        } catch (JsonSchemaException e) {
            assertEquals(e.getMessage(), "a schema locator must be absolute");
        }
    }

    @Test
    public void testJsonPointer()
        throws JsonSchemaException, IOException
    {
        final JsonNode schema = testData.get("test1");

        final JsonNode subSchema, node;

        subSchema = schema.get("schema1").get("properties").get("p2");
        node = schema.get("schema2");
        assertEquals(node, JsonReference.resolveRef(schema, subSchema));

        final ObjectNode tmp = factory.objectNode();

        tmp.put("$ref", "#");
        assertEquals(schema, JsonReference.resolveRef(schema, tmp));

        tmp.put("$ref", "#schema3");
        assertEquals(schema.get("x"), JsonReference.resolveRef(schema, tmp));
    }
}
