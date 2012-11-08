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
import com.google.common.collect.Sets;
import org.eel.kitchen.jsonschema.main.Keyword;
import org.eel.kitchen.jsonschema.metaschema.KeywordRegistry;
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.*;

public abstract class AbstractSyntaxCheckerTest
{
    private final SyntaxValidator syntaxValidator;
    private final JsonNode testData;

    protected AbstractSyntaxCheckerTest(final String resource,
        final KeywordRegistry registry)
        throws IOException
    {
        final String input = "/syntax/" + resource + ".json";
        testData = JsonLoader.fromResource(input);

        syntaxValidator = new SyntaxValidator(registry.getSyntaxCheckers());
    }

    protected AbstractSyntaxCheckerTest(final String resource,
        final String name, final SyntaxChecker checker)
        throws IOException
    {
        final String input = "/syntax/" + resource + ".json";
        testData = JsonLoader.fromResource(input);

        final KeywordRegistry registry = new KeywordRegistry();
        final Keyword keyword = Keyword.withName(name).withSyntaxChecker(checker)
            .build();

        registry.addKeyword(keyword);
        syntaxValidator = new SyntaxValidator(registry.getSyntaxCheckers());
    }

    @DataProvider
    public final Iterator<Object[]> getData()
    {
        final Set<Object[]> set = Sets.newHashSet();

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
    public final void testChecker(final JsonNode node, final boolean valid,
        final JsonNode expectedMessages)
    {
        final List<Message> messages = Lists.newArrayList();
        syntaxValidator.validate(messages, node);
        assertEquals(messages.isEmpty(), valid);

        if (valid)
            return;

        final List<JsonNode> expected = Lists.newArrayList(expectedMessages);
        final List<JsonNode> actual = Lists.newArrayList();

        for (final Message message: messages)
            actual.add(message.toJsonNode());

        assertEqualsNoOrder(actual.toArray(), expected.toArray());
    }
}
