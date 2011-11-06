/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.container;

import eel.kitchen.jsonschema.JsonValidator;
import eel.kitchen.jsonschema.ValidationReport;
import eel.kitchen.util.JsonLoader;
import org.codehaus.jackson.JsonNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.testng.Assert.*;

public final class ArrayTest
{
    private JsonNode testNode;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        testNode = JsonLoader.fromResource("/container/array.json");
    }

    @Test
    public void testItems()
    {
        testOne("items");
    }

    @Test
    public void testMinItems()
    {
        testOne("minItems");
    }

    @Test
    public void testMaxItems()
    {
        testOne("maxItems");
    }

    @Test
    public void testUniqueItems()
    {
        testOne("uniqueItems");
    }

    @Test
    public void testAdditionalItems()
    {
        testOne("additionalItems");
    }

    @Test
    public void testAdditionalItemsSchema()
    {
        testOne("additionalItemsSchema");
    }

    private void testOne(final String testName)
    {
        final JsonNode node = testNode.get(testName);
        final JsonNode schema = node.get("schema");
        final JsonNode good = node.get("good");
        final JsonNode bad = node.get("bad");

        final JsonValidator validator = new JsonValidator(schema);

        ValidationReport report = validator.validate(good);

        assertTrue(report.isSuccess());
        assertTrue(report.getMessages().isEmpty());

        final List<String> expected = new LinkedList<String>();

        for (final JsonNode element: node.get("messages"))
            expected.add(element.getTextValue());

        report = validator.validate(bad);

        assertFalse(report.isSuccess());

        assertEquals(report.getMessages(), expected);
    }
}
