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
import org.eel.kitchen.jsonschema.bundle.KeywordBuilder;
import org.eel.kitchen.jsonschema.bundle.KeywordBundle;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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

    private ValidationContext context;

    @BeforeMethod
    public void setUp()
    {
        bundle = new KeywordBundle();

        checker1 = mock(SyntaxChecker.class);
        k1 = KeywordBuilder.forKeyword("k1").withSyntaxChecker(checker1)
            .build();

        checker2 = mock(SyntaxChecker.class);
        k2 = KeywordBuilder.forKeyword("k2").withSyntaxChecker(checker2)
            .build();

        context = new ValidationContext();
    }

    @Test
    public void shouldInvokeRelevantCheckers()
    {
        final JsonNode instance = factory.objectNode()
            .put("k1", "");

        bundle.registerKeyword(k1);

        validator = new SyntaxValidator(bundle);

        validator.validate(context, instance);

        verify(checker1).checkSyntax(context, instance);
    }

    @Test
    public void shouldIgnoreIrrelevantCheckers()
    {
        final JsonNode instance = factory.objectNode()
            .put("k1", "");

        bundle.registerKeyword(k1);
        bundle.registerKeyword(k2);

        validator = new SyntaxValidator(bundle);

        validator.validate(context, instance);

        verify(checker1).checkSyntax(context, instance);
        verify(checker2, never()).checkSyntax(context, instance);
    }

    @Test
    public void shouldIgnoreKeywordsWithNoSyntaxChecker()
    {
        final JsonNode instance = factory.objectNode().put("k1", "");

        // No syntax checker
        final Keyword k = KeywordBuilder.forKeyword("k1").build();

        bundle.registerKeyword(k);

        validator = new SyntaxValidator(bundle);

        validator.validate(context, instance);

        assertTrue(true);
    }

    @Test
    public void shouldNotValidateSameSchemaAgain()
    {
        final JsonNode instance = factory.objectNode().put("k1", "");

        bundle.registerKeyword(k1);

        validator = new SyntaxValidator(bundle);

        validator.validate(context, instance);
        validator.validate(context, instance);

        verify(checker1, times(1)).checkSyntax(context, instance);
    }
}
