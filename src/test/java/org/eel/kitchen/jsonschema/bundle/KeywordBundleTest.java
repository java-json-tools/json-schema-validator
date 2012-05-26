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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class KeywordBundleTest
{
    private static final String NAME = "keyword";

    private KeywordBundle bundle;

    @BeforeMethod
    public void createBundle()
    {
        bundle = new KeywordBundle();
    }

    @Test
    public void addedKeywordIsRegistered()
    {
        final Keyword keyword = mock(Keyword.class);
        when(keyword.getName()).thenReturn("keyword");

        bundle.registerKeyword(keyword);
        final Map<String,Keyword> keywords = bundle.getKeywords();
        assertTrue(keywords.containsKey("keyword"), "keyword has not been "
            + "registered");
        assertEquals(keywords.get("keyword"), keyword,
            "wrong Keyword registered");
    }

    @Test(dependsOnMethods = "addedKeywordIsRegistered")
    public void cannotRegisterSameKeywordTwice()
    {
        final Keyword k1 = mock(Keyword.class);
        final Keyword k2 = mock(Keyword.class);

        when(k1.getName()).thenReturn("keyword");
        when(k2.getName()).thenReturn("keyword");

        bundle.registerKeyword(k1);

        try {
            bundle.registerKeyword(k2);
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "keyword \"keyword\" already "
                + "registered");
        }
    }

    @Test(dependsOnMethods = "addedKeywordIsRegistered")
    public void canUnregisterKeyword()
    {
        final Keyword keyword = mock(Keyword.class);
        when(keyword.getName()).thenReturn("keyword");

        bundle.registerKeyword(keyword);
        bundle.unregisterKeyword(NAME);
        final Map<String,Keyword> keywords = bundle.getKeywords();

        assertFalse(keywords.containsKey("keyword"), "keyword has not been "
            + "unregistered");
    }
}
