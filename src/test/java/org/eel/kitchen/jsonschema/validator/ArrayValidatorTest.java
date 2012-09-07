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

package org.eel.kitchen.jsonschema.validator;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.ref.SchemaContainer;
import org.eel.kitchen.jsonschema.ref.SchemaNode;
import org.eel.kitchen.jsonschema.util.JacksonUtils;
import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.eel.kitchen.jsonschema.util.NodeAndPath;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.annotations.Sets;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.assertEquals;

/*
 * TODO:
 *
 * - more tests,
 * - do that for object nodes too
 */
public final class ArrayValidatorTest
{
    private JsonNode testData;

    @BeforeClass
    public void initData()
        throws IOException
    {
        testData = JsonLoader.fromResource("/validator/array.json");
    }

    @DataProvider
    public Iterator<Object[]> getData()
    {
        final Set<Object[]> set = Sets.newHashSet();

        for (final JsonNode node: testData)
            set.add(new Object[] {
                node.get("schema"),
                node.get("index").intValue(),
                node.get("expected"),
                node.get("computed").booleanValue()
            });

        return set.iterator();
    }

    @Test(dataProvider = "getData")
    public void arrayElementSchemasAreCorrectlyComputed(final JsonNode schema,
        final int index, final JsonNode expected, final boolean  computed)
    {
        final SchemaContainer container = new SchemaContainer(schema);
        final SchemaNode schemaNode = new SchemaNode(container, schema);
        final ArrayValidator validator = new ArrayValidator(schemaNode);
        final NodeAndPath nodeAndPath = validator.getSchema(index);

        // This is ugly, I know... But ObjectNode's .equals() basically forbids
        // inheritance, so I have to resort to that, since JacksonUtils'
        // .emptyMap() is NOT an ObjectNode :(
        final Map<String, JsonNode> expectedMap
            = JacksonUtils.nodeToMap(expected);
        final Map<String, JsonNode> actualMap
            = JacksonUtils.nodeToMap(nodeAndPath.getNode());

        assertEquals(expectedMap, actualMap);
        assertEquals(computed, nodeAndPath.isComputed());
    }
}
