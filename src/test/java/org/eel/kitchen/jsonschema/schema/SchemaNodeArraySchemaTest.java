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
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.*;

public final class SchemaNodeArraySchemaTest
{
    private JsonNode testData;

    @BeforeClass
    public void initializeTestData()
        throws IOException
    {
        testData = JsonLoader.fromResource("/schemanode/array.json");
    }

    @DataProvider
    private Iterator<Object[]> getData()
        throws JsonSchemaException
    {
        final Set<Object[]> set = new HashSet<Object[]>(testData.size());

        JsonNode schema, expected;
        int index;

        for (final JsonNode node: testData) {
            schema = node.get("schema");
            expected = node.get("expected");
            index = node.get("index").intValue();
            set.add(new Object[] { schema, index, expected });
        }

        return set.iterator();
    }

    @Test(dataProvider = "getData")
    public void testArraySchema(final JsonNode schema, final int index,
        final JsonNode expected)
        throws JsonSchemaException
    {
        final String errmsg = "array schema lookup failure (schema: "
            + schema + ", index: " + index + ")";
        final SchemaContainer container = new SchemaContainer(schema);
        final SchemaNode node = new SchemaNode(container, schema);

        final JsonNode actual = node.getArraySchema(index);

        assertEquals(actual, expected, errmsg);
    }
}
