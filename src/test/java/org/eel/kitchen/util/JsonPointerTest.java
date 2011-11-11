/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.util;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.*;

public final class JsonPointerTest
{
    @Test(
        expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "^illegal JSON Pointer haha$"
    )
    public void testInvalidPath()
    {
        new JsonPointer("haha");
    }

    @Test
    public void testNullPathEqualsEmptyPath()
    {
        final JsonPointer p1 = new JsonPointer(null);
        final JsonPointer p2 = new JsonPointer("");

        assertEquals(p1, p2);
        assertEquals("#", p1.toString());
        assertEquals("#", p2.toString());
    }

    @Test
    public void testEqualsHashCode()
    {
        final JsonPointer p1 = new JsonPointer(null);
        final JsonPointer p2 = new JsonPointer("");
        final JsonPointer p3 = new JsonPointer("#");

        final Set<JsonPointer> set = new HashSet<JsonPointer>();
        set.add(p1);

        assertFalse(set.add(p2));

        /*
         * Note that Test-NG has assertNotEquals(). Unfortunately, it doesn't
         * quite do the right thing: it enforces two parts of the .equals()
         * contract which should be left to implementations! Mainly, that if
         * objects are of different types then equals is false,
         * and that o.equals(null) is always false...
         *
         * Therefore we have to use assertFalse() here.
         */
        assertFalse(p1.equals(""));
        assertFalse(p2.equals(null));

        assertTrue(p1.equals(p2) && p2.equals(p3));
        assertTrue(p1.equals(p3));
    }

    @Test
    public void testEndingSlash()
    {
        final JsonPointer p1 = new JsonPointer("");
        final JsonPointer p2 = new JsonPointer("/");

        assertNotEquals(p1, p2);
    }

    @Test
    public void testEncodedVsDecoded()
    {
        final String decoded = "/a'b&c, d";
        final String encoded = "/a%27b%26c%2c%20d";

        final JsonPointer p1 = new JsonPointer(decoded);
        final JsonPointer p2 = new JsonPointer(encoded);

        assertEquals(p1, p2);
        assertEquals(p1.toString(), "#" + encoded);
        assertEquals(p2.toDecodedString(), "#" + decoded);
    }

    @Test
    public void testSlashInPathElement()
    {
        final String path = "/a%2FbC";
        final String normalizedPath = "#/a%2fbC";
        final JsonPointer p = new JsonPointer(path);

        assertEquals(p.toString(), normalizedPath);
        assertEquals(p.toDecodedString(), normalizedPath);

        final String path2 = "/a/bC";
        assertNotEquals(p, new JsonPointer(path2));
    }

    @Test
    public void testPercentInPathElement()
    {
        final String path = "/a%b";
        final String normalizedPath="#/a%25b";

        final JsonPointer p1 = new JsonPointer(path);
        final JsonPointer p2 = new JsonPointer(normalizedPath);

        assertEquals(p1, p2);
        assertEquals(p1.toString(), normalizedPath);
        assertEquals(p2.toDecodedString(), "#" + path);
    }

    @Test
    public void testGetPath()
        throws IOException
    {
        final String path = "#/a%2F//..";
        final JsonNode node = JsonLoader.fromResource("/jsonpointer/pointer"
            + ".json");
        final JsonNode expected = JsonNodeFactory.instance.textNode("hello "
            + "world");

        final JsonPointer pointer = new JsonPointer(path);

        assertEquals(pointer.getPath(node), expected);
    }

    @Test
    public void testAppend()
        throws IOException
    {
        final JsonNode node = JsonLoader.fromResource("/jsonpointer/pointer"
            + ".json");
        final JsonNode expected = JsonNodeFactory.instance.textNode("hello "
            + "world");

        final String basePath = "/a%2F/";
        final String append = "..";

        final JsonPointer base = new JsonPointer(basePath);
        final JsonPointer p = base.append(append);

        assertEquals(p.getPath(node), expected);
    }
}
