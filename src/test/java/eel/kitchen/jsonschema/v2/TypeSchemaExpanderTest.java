/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.v2;

import eel.kitchen.util.CollectionUtils;
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;

public final class TypeSchemaExpanderTest
{
    private JsonNode allTests;
    private final Set<JsonNode> actual = new HashSet<JsonNode>();
    private final Set<JsonNode> expected = new HashSet<JsonNode>();

    @BeforeClass
    public void setUp()
        throws IOException
    {
        allTests = JasonHelper.load("v2/type-expander.json");
    }

    @Test
    public void testSimpleType()
    {
        doTest("simpletype");
    }

    @Test
    public void testNoType()
    {
        doTest("notype");
    }

    @Test
    public void testTypeAny()
    {
        doTest("typeany");
    }

    @Test
    public void testTypeAny2()
    {
        doTest("typeany2");
    }

    @Test
    public void testComplexType()
    {
        doTest("complextype");
    }

    @Test
    public void testComplexType2()
    {
        doTest("complextype2");
    }

    private void doTest(final String testName)
    {
        final JsonNode test = allTests.get(testName);
        final JsonNode input = test.get("input");
        final JsonNode output = test.get("output");
        actual.clear();
        expected.clear();

        actual.addAll(CollectionUtils.toSet(new TypeSchemaExpander(input)));

        expected.addAll(CollectionUtils.toSet(output.getElements()));

        assertEquals(actual, expected);
    }
}
