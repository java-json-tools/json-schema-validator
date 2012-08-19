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

package org.eel.kitchen.jsonschema.syntax;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.*;

public abstract class AbstractSyntaxCheckerTest
{
    private final JsonNode testData;
    private final SyntaxChecker checker;

    private List<String> messages;

    protected AbstractSyntaxCheckerTest(final String keyword,
        final SyntaxChecker checker)
        throws IOException
    {
        final String input = "/syntax/" + keyword + ".json";
        testData = JsonLoader.fromResource(input);
        this.checker = checker;
    }

    @BeforeMethod
    public void createValidator()
    {
        messages = new ArrayList<String>();
    }

    @DataProvider
    public Iterator<Object[]> getData()
    {
        final Set<Object[]> set = new HashSet<Object[]>(testData.size());

        for (final JsonNode node: testData)
            set.add(mungeArguments(node));

        return set.iterator();
    }

    private static Object[] mungeArguments(final JsonNode node)
    {
        return new Object[] {
            node.get("schema"),
            node.get("valid").booleanValue()
        };
    }

    @Test(dataProvider = "getData")
    public void testChecker(final JsonNode node, final boolean valid)
    {
        checker.checkSyntax(messages, node);
        assertEquals(messages.isEmpty(), valid);
    }
}
