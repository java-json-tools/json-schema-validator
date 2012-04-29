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

package org.eel.kitchen.util;

import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

public final class JsonPointerV2Test
{
    @Test
    public void testEqualsBasics()
        throws JsonSchemaException
    {
        final JsonPointerV2 p1 = new JsonPointerV2("#/");
        final JsonPointerV2 p2 = new JsonPointerV2("#/");

        assertTrue(p1.equals(p1));
        assertTrue(p1.equals(p2));

        assertFalse(p1.equals(null));
        assertFalse(p1.equals(new Object()));
    }

    @Test
    public void testHashCode()
        throws JsonSchemaException
    {
        final JsonPointerV2 p1 = new JsonPointerV2("#/");
        final JsonPointerV2 p2 = new JsonPointerV2("#/");

        final String s = "#/";
        assertTrue(p1.hashCode() == p2.hashCode());
        assertTrue(p1.hashCode() == s.hashCode());
        assertFalse(p1.equals(s));
    }

    @Test
    public void testInitialHashDoesNotMatter()
        throws JsonSchemaException
    {
        final JsonPointerV2 p1 = new JsonPointerV2("#/a/b");
        final JsonPointerV2 p2 = new JsonPointerV2("/a/b");

        assertEquals(p1, p2);
        assertEquals(p1.toString(), p2.toString());
    }

    @Test
    public void testToString()
        throws JsonSchemaException
    {
        final String s1 = "#/a/b";
        final String s2 = "/a/b";

        final JsonPointerV2 p1 = new JsonPointerV2(s1);
        final JsonPointerV2 p2 = new JsonPointerV2(s2);

        assertEquals(s1, p1.toString());
        assertEquals(s1, p2.toString());
    }

    @Test
    public void testCaretEscape()
        throws JsonSchemaException
    {
        final String[] array = { "a^", "^a", "a^a", "^" };
        final List<String> expected = Arrays.asList(array);

        final JsonPointerV2 v2 = new JsonPointerV2("#/a^^/^^a/a^^a/^^");
        final List<String> actual = v2.getElements();

        assertEquals(actual, expected);
    }

    @Test
    public void testSlashEscape()
        throws JsonSchemaException
    {
        final String[] array = { "a/", "/a", "a/a", "/" };
        final List<String> expected = Arrays.asList(array);

        final JsonPointerV2 v2 = new JsonPointerV2("#/a^//^/a/a^/a/^/");
        final List<String> actual = v2.getElements();

        assertEquals(actual, expected);
    }

    @Test
    public void testMissingSlash()
    {
        try {
            new JsonPointerV2("#a");
            fail("No exception thrown!");
        } catch (JsonSchemaException ignored) {
            assertTrue(true);
        }
    }

    @Test
    public void testIllegalEscape()
    {
        try {
            new JsonPointerV2("#/^a");
            fail("No exception thrown!");
        } catch (JsonSchemaException ignored) {
            assertTrue(true);
        }

        try {
            new JsonPointerV2("#/^");
            fail("No exception thrown!");
        } catch (JsonSchemaException ignored) {
            assertTrue(true);
        }
    }
}
