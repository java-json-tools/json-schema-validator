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

package org.eel.kitchen.jsonschema.ref;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.eel.kitchen.jsonschema.JsonSchemaException;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.*;

public final class JsonRefTest
{
    private final JsonNodeFactory factory = JsonNodeFactory.instance;

    @Test
    public void nonStringMembersShouldBeIdentifiedAsInvalid()
    {
        final JsonNode node = factory.objectNode().put("$ref", 1);

        try {
            JsonRef.fromNode(node, "$ref");
            fail("No exception thrown!");
        } catch (JsonSchemaException e) {
            assertEquals(e.getMessage(), "invalid $ref entry: not a string");
        }
    }

    @Test
    public void NonURIStringsShouldBeIdentifiedAsInvalid()
    {
        try {
            new JsonRef("+23:");
            fail("No exception thrown!");
        } catch (JsonSchemaException e) {
            assertEquals(e.getMessage(), "invalid URI: +23:");
        }
    }

    @Test
    public void twoJsonRefsWithSameURIAreEqualAndHaveTheSameHashCode()
        throws JsonSchemaException
    {
        final URI uri = URI.create("foo");
        final JsonRef ref1 = new JsonRef(uri);
        final JsonRef ref2 = new JsonRef("foo");

        assertTrue(ref1.equals(ref2));
        assertEquals(ref1.hashCode(), ref2.hashCode());
    }

    @Test
    public void afterURINormalizationJsonRefsShouldBeEqual()
        throws JsonSchemaException
    {
        final String s1 = "http://foo.bar/a/b";
        final String s2 = "http://foo.bar/c/../a/./b";

        final JsonRef ref1 = new JsonRef(s1);
        final JsonRef ref2 = new JsonRef(s2);
        assertEquals(ref1, ref2);
    }

    @Test
    public void absoluteRefsShouldBeIdentifiedAsSuch()
        throws JsonSchemaException
    {
        final String s1 = "http://foo.bar/a/b";
        final String s2 = "foo.bar";

        final JsonRef ref1 = new JsonRef(s1);
        final JsonRef ref2 = new JsonRef(s2);

        assertTrue(ref1.isAbsolute());
        assertFalse(ref2.isAbsolute());
    }

    @Test
    public void absoluteURIWithFragmentIsNotAnAbsoluteRef()
        throws JsonSchemaException
    {
        final JsonRef ref = new JsonRef("http://foo.bar/a/b#c");

        assertFalse(ref.isAbsolute());
    }

    @Test
    public void testFragments()
        throws JsonSchemaException
    {
        JsonRef ref;

        ref = new JsonRef("file:///a");
        assertFalse(ref.hasFragment());

        ref = new JsonRef("file:///a#");
        assertFalse(ref.hasFragment());

        ref = new JsonRef("file:///a#b/c");
        assertTrue(ref.hasFragment());
        assertEquals(ref.getFragment().toString(), "#b/c");
    }

    @Test
    public void emptyOrNoFragmentIsTheSame()
        throws JsonSchemaException
    {
        final JsonRef ref1 = new JsonRef("http://foo.bar");
        final JsonRef ref2 = new JsonRef("http://foo.bar#");

        assertEquals(ref1, ref2);
    }
}
