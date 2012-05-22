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
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.*;

public final class SyntaxValidatorTest
{
    private JsonNode testData;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        testData = JsonLoader.fromResource("/syntax/syntax.json");
    }

    @DataProvider
    public Iterator<Object[]> getData()
    {
        final Set<Object[]> set = new HashSet<Object[]>(testData.size());

        for (final JsonNode node: testData)
            set.add(mungeArguments(node));

        return set.iterator();
    }

    private Object[] mungeArguments(final JsonNode node)
    {
        return new Object[] {
            node.get("schema"),
            node.get("valid").booleanValue()
        };
    }

    @Test(dataProvider = "getData")
    public void testEntry(final JsonNode schemaNode, final boolean valid)
    {
        final ValidationReport report = new ValidationReport();

        SyntaxValidator.validate(report, schemaNode);

        assertEquals(report.isSuccess(), valid, "syntax validation failure "
            + "for schema " + schemaNode);
    }
}
