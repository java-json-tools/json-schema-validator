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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableSet;
import org.eel.kitchen.jsonschema.util.CustomJsonNodeFactory;
import org.eel.kitchen.jsonschema.util.JacksonUtils;
import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.annotations.Sets;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.*;

/*
 * TODO:
 *
 * - more tests,
 * - do that for object nodes too
 */
public final class ObjectValidatorTest
{
    private JsonNode testData;

    @BeforeClass
    public void initData()
        throws IOException
    {
        testData = JsonLoader.fromResource("/validator/object.json");
    }

    @DataProvider
    public Iterator<Object[]> getData()
    {
        final Set<Object[]> set = Sets.newHashSet();

        for (final JsonNode node: testData)
            set.add(new Object[] {
                node.get("schema"),
                node.get("member").textValue(),
                node.get("expected")
            });

        return set.iterator();
    }

    @Test(dataProvider = "getData")
    public void objectChildrenSchemasAreCorrectlyComputed(final JsonNode schema,
        final String member, final JsonNode expected)
    {
        final ObjectValidator validator = new ObjectValidator(schema);
        final Set<JsonNode> actual = validator.getSchemas(member);

        checkNodeAndPaths(actual, expected);
    }

    // FIXME: necessary because of ObjectNode and .equals()
    private static void checkNodeAndPaths(final Set<JsonNode> actual,
        final JsonNode expected)
    {
        assertEquals(actual.size(), expected.size());

        final Set<JsonNode> expectedSet = ImmutableSet.copyOf(expected);
        final Set<JsonNode> actualSet = Sets.newHashSet();

        Map<String, JsonNode> map;
        ObjectNode node;

        for (final JsonNode element: actual) {
            node = CustomJsonNodeFactory.getInstance().objectNode();
            map = JacksonUtils.nodeToMap(element);
            node.putAll(map);
            actualSet.add(node);
        }

        assertEqualsNoOrder(actualSet.toArray(), expectedSet.toArray());
    }
}
