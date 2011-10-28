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
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.*;

public final class ExtendsSchemaExpanderTest
{
    private JsonNode allTests;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        allTests = JasonHelper.load("v2/extends-expander.json");
    }

    @Test
    public void testNoExtends()
    {
        doTest("noextend");
    }

    @Test
    public void testExtendsOne()
    {
        doTest("extendsOne");
    }

    @Test
    public void testExtendsSeveral()
    {
        doTest("extendsSeveral");
    }

    private void doTest(final String test)
    {
        final JsonNode testNode = allTests.get(test);

        final Iterator<CombinedSchema> iterator
            = new ExtendsSchemaExpander(testNode.get("input"));

        final Set<Set<JsonNode>> actual = new HashSet<Set<JsonNode>>();

        while (iterator.hasNext()) {
            actual.add(CollectionUtils.toSet(iterator.next()));
        }

        final JsonNode outputs = testNode.get("output");

        final Set<Set<JsonNode>> expected = new HashSet<Set<JsonNode>>();

        for (final JsonNode output: outputs)
            expected.add(CollectionUtils.toSet(output.getElements()));

        assertEquals(actual, expected);
    }
}
