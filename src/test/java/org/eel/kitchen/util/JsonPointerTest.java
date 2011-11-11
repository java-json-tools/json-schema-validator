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

import org.testng.annotations.Test;

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
}
