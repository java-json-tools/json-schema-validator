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
import org.eel.kitchen.jsonschema.bundle.Keyword;
import org.eel.kitchen.jsonschema.bundle.KeywordBundle;
import org.eel.kitchen.jsonschema.report.ValidationMessage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class SyntaxValidatorTest
{
    private static final JsonNodeFactory factory = JsonNodeFactory.instance;

    private KeywordBundle bundle;
    private SyntaxValidator validator;

    private Keyword k1;
    private Keyword k2;

    private SyntaxChecker checker1;
    private SyntaxChecker checker2;

    private List<ValidationMessage> messages;

    @BeforeMethod
    public void setUp()
    {
        bundle = new KeywordBundle();

        checker1 = mock(SyntaxChecker.class);
        k1 = Keyword.Builder.forKeyword("k1").withSyntaxChecker(checker1)
            .build();

        checker2 = mock(SyntaxChecker.class);
        k2 = Keyword.Builder.forKeyword("k2").withSyntaxChecker(checker2)
            .build();

        messages = new ArrayList<ValidationMessage>();
    }

    @Test
    public void shouldInvokeRelevantCheckers()
    {
        final JsonNode instance = factory.objectNode()
            .put("k1", "");

        bundle.registerKeyword(k1);

        validator = new SyntaxValidator(bundle);

        validator.validate(messages, instance);

        verify(checker1).checkSyntax(any(ValidationMessage.Builder.class),
            eq(messages), eq(instance));
    }

    @Test
    public void shouldIgnoreIrrelevantCheckers()
    {
        final JsonNode instance = factory.objectNode()
            .put("k1", "");

        bundle.registerKeyword(k1);
        bundle.registerKeyword(k2);

        validator = new SyntaxValidator(bundle);

        validator.validate(messages, instance);

        verify(checker1).checkSyntax(any(ValidationMessage.Builder.class),
            eq(messages), eq(instance));
        verify(checker2, never())
            .checkSyntax(any(ValidationMessage.Builder.class), eq(messages),
                eq(instance));
    }

    @Test
    public void shouldIgnoreKeywordsWithNoSyntaxChecker()
    {
        final JsonNode instance = factory.objectNode().put("k1", "");

        // No syntax checker
        final Keyword k = Keyword.Builder.forKeyword("k1").build();

        bundle.registerKeyword(k);

        validator = new SyntaxValidator(bundle);

        validator.validate(messages, instance);

        assertTrue(true);
    }
}
