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
import com.google.common.collect.Lists;
import org.eel.kitchen.jsonschema.report.ValidationDomain;
import org.eel.kitchen.jsonschema.report.ValidationMessage;
import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.*;

public abstract class AbstractSyntaxCheckerTest
{
    private final String keyword;
    private final JsonNode testData;
    private final SyntaxChecker checker;

    protected AbstractSyntaxCheckerTest(final String keyword,
        final String resourceName, final SyntaxChecker checker)
        throws IOException
    {
        this.keyword = keyword;
        this.checker = checker;
        final String input = "/syntax/" + resourceName + ".json";
        testData = JsonLoader.fromResource(input);
    }

    protected AbstractSyntaxCheckerTest(final String keyword,
        final SyntaxChecker checker)
        throws IOException
    {
        this(keyword, keyword, checker);
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
            node.get("valid").booleanValue(),
            node.get("messages")
        };
    }

    @Test(dataProvider = "getData", invocationCount = 10, threadPoolSize = 4)
    public void testChecker(final JsonNode node, final boolean valid,
        final JsonNode expectedMessages)
    {
        final List<ValidationMessage> messages = Lists.newArrayList();
        final ValidationMessage.Builder msg
            = new ValidationMessage.Builder(ValidationDomain.SYNTAX)
                .setKeyword(keyword);
        checker.checkSyntax(msg, messages, node);
        assertEquals(messages.isEmpty(), valid);

        if (valid)
            return;

        final List<JsonNode> expected = Lists.newArrayList(expectedMessages);
        final List<JsonNode> actual = Lists.newArrayList();

        for (final ValidationMessage message: messages)
            actual.add(message.toJsonNode());

        assertEqualsNoOrder(actual.toArray(), expected.toArray());
    }
}
