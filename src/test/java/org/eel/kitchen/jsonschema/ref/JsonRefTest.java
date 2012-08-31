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

import com.google.common.collect.ImmutableSet;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.report.ValidationDomain;
import org.eel.kitchen.jsonschema.report.ValidationMessage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Iterator;

import static org.testng.Assert.*;

public final class JsonRefTest
{
    private JsonRef baseRef;

    @BeforeClass
    public void initializeBaseRef()
        throws JsonSchemaException
    {
        baseRef = JsonRef.fromString("http://foo.bar/baz#");
    }

    @Test
    public void NonURIStringsShouldBeIdentifiedAsInvalid()
    {
        try {
            JsonRef.fromString("+23:");
            fail("No exception thrown!");
        } catch (JsonSchemaException e) {
            final ValidationMessage msg = e.getValidationMessage();
            assertSame(msg.getDomain(), ValidationDomain.REF_RESOLVING);
            assertEquals(msg.getKeyword(), "N/A");
            assertEquals(msg.getMessage(), "invalid URI");
            assertEquals(msg.getInfo("uri").textValue(), "+23:");
        }
    }

    @Test
    public void twoJsonRefsWithSameURIAreEqualAndHaveTheSameHashCode()
        throws JsonSchemaException
    {
        final URI uri = URI.create("foo");
        final JsonRef ref1 = JsonRef.fromURI(uri);
        final JsonRef ref2 = JsonRef.fromString("foo");

        assertTrue(ref1.equals(ref2));
        assertEquals(ref1.hashCode(), ref2.hashCode());
    }

    @Test
    public void afterURINormalizationJsonRefsShouldBeEqual()
        throws JsonSchemaException
    {
        final String s1 = "http://foo.bar/a/b";
        final String s2 = "http://foo.bar/c/../a/./b";

        final JsonRef ref1 = JsonRef.fromString(s1);
        final JsonRef ref2 = JsonRef.fromString(s2);
        assertEquals(ref1, ref2);
    }

    @Test
    public void absoluteRefsShouldBeIdentifiedAsSuch()
        throws JsonSchemaException
    {
        final String s1 = "http://foo.bar/a/b";
        final String s2 = "foo.bar";

        final JsonRef ref1 = JsonRef.fromString(s1);
        final JsonRef ref2 = JsonRef.fromString(s2);

        assertTrue(ref1.isAbsolute());
        assertFalse(ref2.isAbsolute());
    }

    @Test
    public void absoluteURIWithFragmentIsNotAnAbsoluteRef()
        throws JsonSchemaException
    {
        final JsonRef ref = JsonRef.fromString("http://foo.bar/a/b#c");

        assertFalse(ref.isAbsolute());
    }

    @Test
    public void testFragments()
        throws JsonSchemaException
    {
        JsonRef ref;
        JsonFragment fragment;

        ref = JsonRef.fromString("file:///a");
        fragment = ref.getFragment();
        assertTrue(fragment.isEmpty());

        ref = JsonRef.fromString("file:///a#");
        fragment = ref.getFragment();
        assertTrue(fragment.isEmpty());

        ref = JsonRef.fromString("file:///a#b/c");
        fragment = ref.getFragment();
        assertFalse(fragment.isEmpty());
        assertEquals(fragment.toString(), "b/c");
    }

    @Test
    public void emptyOrNoFragmentIsTheSame()
        throws JsonSchemaException
    {
        final JsonRef ref1 = JsonRef.fromString("http://foo.bar");
        final JsonRef ref2 = JsonRef.fromString("http://foo.bar#");

        assertEquals(ref1, ref2);
    }

    @DataProvider
    public Iterator<Object[]> getData()
    {
        final ImmutableSet.Builder<Object[]> builder
            = new ImmutableSet.Builder<Object[]>();

        builder.add(new Object[] { "http://foo.bar/blah#", false });
        builder.add(new Object[] { "http://foo.bar/baz#pwet", true });
        builder.add(new Object[] { "http://foo.bar/baz?a=b", false });
        builder.add(new Object[] { "#/a/b/c", true });
        builder.add(new Object[] { "a/b/v", false });
        builder.add(new Object[] { "baz", true });

        return builder.build().iterator();
    }

    @Test(dataProvider = "getData")
    public void testReferenceContains(final String input,
        final boolean contained)
        throws JsonSchemaException
    {
        final JsonRef tmp = JsonRef.fromString(input);
        final JsonRef resolved = baseRef.resolve(tmp);

        assertEquals(baseRef.contains(resolved), contained);
    }
}
