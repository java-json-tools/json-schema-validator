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
import org.eel.kitchen.util.CollectionUtils;
import org.eel.kitchen.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.*;

public final class SchemaNodeObjectSchemaTest
{
    private JsonNode testData;

    @BeforeClass
    public void initializeTestData()
        throws IOException
    {
        testData = JsonLoader.fromResource("/schemanode/object.json");
    }

    @DataProvider
    private Iterator<Object[]> getData()
        throws JsonSchemaException
    {
        final Set<Object[]> set = new HashSet<Object[]>(testData.size());

        JsonNode schema;
        Set<JsonNode> expected;
        String key;

        for (final JsonNode node: testData) {
            schema = node.get("schema");
            expected = CollectionUtils.toSet(node.get("expected").elements());
            key = node.get("key").textValue();
            set.add(new Object[] { schema, key, expected });
        }

        return set.iterator();
    }

    @Test(dataProvider = "getData")
    public void testArraySchema(final JsonNode schema, final String key,
        final Set<JsonNode> expected)
        throws JsonSchemaException
    {
        final String errmsg = "object schema lookup failure (schema: "
            + schema + ", key: " + key + ")";
        final SchemaContainer container
            = SchemaContainer.anonymousSchema(schema);
        final SchemaNode node = new SchemaNode(container, schema);

        final Set<JsonNode> actual = node.getObjectSchemas(key);

        assertEquals(actual, expected, errmsg);
    }
}
