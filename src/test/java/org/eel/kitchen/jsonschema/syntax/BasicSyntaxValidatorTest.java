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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.eel.kitchen.jsonschema.metaschema.KeywordRegistry;
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

public final class BasicSyntaxValidatorTest
{
    private final SyntaxValidator validator;

    public BasicSyntaxValidatorTest()
    {
        final KeywordRegistry registry = new KeywordRegistry();
        validator = new SyntaxValidator(registry.getSyntaxCheckers());
    }

    @DataProvider
    private Iterator<Object[]> getData()
    {
        final JsonNodeFactory factory = JsonNodeFactory.instance;

        return new ImmutableSet.Builder<Object[]>()
            .add(new Object[] { factory.arrayNode() })
            .add(new Object[] { factory.booleanNode(true) })
            .add(new Object[] { factory.numberNode(1) })
            .add(new Object[] { factory.numberNode(1.0) })
            .add(new Object[] { factory.nullNode() })
            .add(new Object[] { factory.textNode("") })
            .build().iterator();
    }

    @Test(dataProvider = "getData", invocationCount = 10, threadPoolSize = 4)
    public void syntaxCheckingCorrectlyBalksOnNonObject(final JsonNode schema)
    {
        final NodeType nodeType = NodeType.getNodeType(schema);
        final List<Message> messages = Lists.newArrayList();

        validator.validate(messages, schema);

        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0).getInfo("found").textValue(),
            nodeType.toString());
    }
}
