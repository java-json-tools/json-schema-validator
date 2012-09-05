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

package org.eel.kitchen.jsonschema.bundle;

import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class KeywordBundleTest
{
    private static final String KEYWORD = "keyword";
    private final KeywordBundle bundle = new KeywordBundle();

    @Test
    public void cannotBuildKeywordWithNullName()
    {
        try {
            Keyword.withName(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "keyword name must not be null");
        }
    }

    @Test
    public void emptyKeywordDoesNotShowInAnyMap()
    {
        final Keyword keyword = Keyword.withName(KEYWORD).build();

        bundle.registerKeyword(keyword);

        assertFalse(bundle.getSyntaxCheckers().containsKey(KEYWORD));
        assertFalse(bundle.getValidators().containsKey(KEYWORD));
    }

    @Test
    public void keywordWithSyntaxCheckerOnlyDoesNotShowUpInValidators()
    {
        final SyntaxChecker checker = mock(SyntaxChecker.class);
        final Keyword keyword = Keyword.withName(KEYWORD)
            .withSyntaxChecker(checker).build();

        bundle.registerKeyword(keyword);

        assertSame(bundle.getSyntaxCheckers().get(KEYWORD), checker);
        assertFalse(bundle.getValidators().containsKey(KEYWORD));
    }

    @Test(dependsOnMethods = "keywordWithSyntaxCheckerOnlyDoesNotShowUpInValidators")
    public void keywordWithValidatorOnlyDoesNotShowUpInSyntaxCheckers()
    {
        final Keyword keyword = Keyword.withName(KEYWORD)
            .withValidatorClass(KeywordValidator.class).build();

        bundle.registerKeyword(keyword);

        assertFalse(bundle.getSyntaxCheckers().containsKey(KEYWORD));
        assertSame(bundle.getValidators().get(KEYWORD), KeywordValidator.class);
    }

    @Test
    public void keywordIsCorrectlyUnregistered()
    {
        final SyntaxChecker checker = mock(SyntaxChecker.class);
        final Keyword keyword = Keyword.withName(KEYWORD)
            .withSyntaxChecker(checker)
            .withValidatorClass(KeywordValidator.class).build();

        bundle.registerKeyword(keyword);

        assertSame(bundle.getSyntaxCheckers().get(KEYWORD), checker);
        assertSame(bundle.getValidators().get(KEYWORD), KeywordValidator.class);

        bundle.unregisterKeyword(KEYWORD);

        assertFalse(bundle.getSyntaxCheckers().containsKey(KEYWORD));
        assertFalse(bundle.getValidators().containsKey(KEYWORD));
    }
}
