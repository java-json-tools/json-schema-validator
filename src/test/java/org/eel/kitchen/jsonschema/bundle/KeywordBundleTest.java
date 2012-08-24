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
    public void cannotBuildKeywordWithNullName()
    {
        try {
            Keyword.Builder.forKeyword(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "keyword name must not be null");
        }
    }

    @Test
    public void addedKeywordIsRegistered()
    {
        final Keyword keyword = Keyword.Builder.forKeyword(NAME).build();

        boolean found = false;

        bundle.registerKeyword(keyword);

        for (final Map.Entry<String, Keyword> entry: bundle) {
            if (!NAME.equals(entry.getKey()))
                continue;
            found = true;
            assertSame(entry.getValue(), keyword, "wrong keyword registered");
        }
        assertTrue(found, "keyword not registered");
    }

    @Test(dependsOnMethods = "addedKeywordIsRegistered")
    public void canUnregisterKeyword()
    {
        final Keyword keyword = Keyword.Builder.forKeyword(NAME).build();

        boolean found = false;

        bundle.registerKeyword(keyword);
        bundle.unregisterKeyword(NAME);
        for (final Map.Entry<String, Keyword> entry: bundle)
            if (NAME.equals(entry.getKey())) {
                found = true;
                break;
            }

        assertFalse(found, "keyword has not been unregistered");
    }
}
