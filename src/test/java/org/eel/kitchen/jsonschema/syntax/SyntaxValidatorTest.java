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
import org.eel.kitchen.jsonschema.SampleNodeProvider;
import org.eel.kitchen.jsonschema.main.Keyword;
import org.eel.kitchen.jsonschema.metaschema.KeywordRegistry;
import org.eel.kitchen.jsonschema.report.Domain;
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class SyntaxValidatorTest
{
    private static final JsonNodeFactory factory = JsonNodeFactory.instance;

    private KeywordRegistry registry;
    private SyntaxValidator validator;

    private Keyword k1;
    private Keyword k2;

    private SyntaxChecker checker1;
    private SyntaxChecker checker2;

    private List<Message> messages;

    @BeforeMethod
    public void setUp()
    {
        registry = new KeywordRegistry();

        checker1 = mock(SyntaxChecker.class);
        k1 = Keyword.withName("k1").withSyntaxChecker(checker1).build();

        checker2 = mock(SyntaxChecker.class);
        k2 = Keyword.withName("k2").withSyntaxChecker(checker2).build();

        messages = new ArrayList<Message>();
    }

    @DataProvider
    private Iterator<Object[]> getNonSchemaJsonDocuments()
    {
        return SampleNodeProvider.getSamplesExcept(NodeType.OBJECT);
    }


    @Test(dataProvider = "getNonSchemaJsonDocuments")
    public void syntaxCheckingCorrectlyBalksOnNonObject(final JsonNode schema)
    {
        final NodeType nodeType = NodeType.getNodeType(schema);

        validator = new SyntaxValidator(new KeywordRegistry());

        validator.validate(messages, schema);

        final Message message = Domain.SYNTAX.newMessage().setKeyword("N/A")
            .addInfo("found", nodeType)
            .setMessage("illegal JSON Schema: not an object").build();

        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), message);
    }

    @Test
    public void shouldInvokeRelevantCheckers()
    {
        final JsonNode instance = factory.objectNode()
            .put("k1", "");

        registry.addKeyword(k1);

        validator = new SyntaxValidator(registry);

        validator.validate(messages, instance);

        verify(checker1).checkSyntax(any(Message.Builder.class), eq(messages),
            eq(instance));
    }

    @Test
    public void shouldIgnoreIrrelevantCheckers()
    {
        final JsonNode instance = factory.objectNode()
            .put("k1", "");

        registry.addKeyword(k1);
        registry.addKeyword(k2);

        validator = new SyntaxValidator(registry);

        validator.validate(messages, instance);

        verify(checker1).checkSyntax(any(Message.Builder.class), eq(messages),
            eq(instance));
        verify(checker2, never())
            .checkSyntax(any(Message.Builder.class), eq(messages),
                eq(instance));
    }

    @Test
    public void shouldIgnoreKeywordsWithNoSyntaxChecker()
    {
        final JsonNode instance = factory.objectNode().put("k1", "");

        // No syntax checker
        final Keyword k = Keyword.withName("k1").build();

        registry.addKeyword(k);

        validator = new SyntaxValidator(registry);

        validator.validate(messages, instance);

        assertTrue(true);
    }
}
