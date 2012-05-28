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

package org.eel.kitchen.jsonschema.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.ref.JsonRef;
import org.testng.annotations.Test;

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
    public void NonURIStringMembersShouldBeIdentifiedAsInvalid()
    {
        final JsonNode node = factory.objectNode().put("$ref", "+23:");

        try {
            JsonRef.fromNode(node, "$ref");
            fail("No exception thrown!");
        } catch (JsonSchemaException e) {
            assertEquals(e.getMessage(), "invalid $ref entry: not a valid URI");
        }
    }

    @Test
    public void twoJsonRefsWithSameURIAreEqualAndHaveTheSameHashCode()
        throws JsonSchemaException
    {
        final JsonNode node = factory.objectNode().put("$ref", "foo");

        final JsonRef ref1 = JsonRef.fromNode(node, "$ref");
        final JsonRef ref2 = JsonRef.fromNode(node, "$ref");

        assertTrue(ref1.equals(ref2));
        assertEquals(ref1.hashCode(), ref2.hashCode());
    }

    @Test
    public void equalsImplementationShouldBeReflexive()
        throws JsonSchemaException
    {
        final JsonNode node = factory.objectNode().put("$ref", "foo");

        final JsonRef ref = JsonRef.fromNode(node, "$ref");

        assertTrue(ref.equals(ref));
    }

    @Test
    public void equalsImplementationShouldBeSymmetric()
        throws JsonSchemaException
    {
        final JsonNode node = factory.objectNode().put("$ref", "foo");

        final JsonRef ref1 = JsonRef.fromNode(node, "$ref");
        final JsonRef ref2 = JsonRef.fromNode(node, "$ref");

        // a => b is equal to !a || b
        assertTrue(!ref1.equals(ref2) || ref2.equals(ref1));
    }

    @Test
    public void equalsImplementationShouldBeTransitive()
        throws JsonSchemaException
    {
        final JsonNode node = factory.objectNode().put("$ref", "foo");

        final JsonRef ref1 = JsonRef.fromNode(node, "$ref");
        final JsonRef ref2 = JsonRef.fromNode(node, "$ref");
        final JsonRef ref3 = JsonRef.fromNode(node, "$ref");

        assertTrue(ref1.equals(ref2));
        assertTrue(ref2.equals(ref3));
        assertTrue(ref1.equals(ref3));
    }

    @Test
    public void equalsImplementationShouldHandleNullAndDifferentClass()
        throws JsonSchemaException
    {
        final JsonNode node = factory.objectNode().put("$ref", "foo");

        final JsonRef ref = JsonRef.fromNode(node, "$ref");

        assertFalse(ref.equals(null));
        assertFalse(ref.equals(new Object()));
    }

    @Test
    public void normalizedURIsShouldBeIdentifiedAsSuch()
        throws JsonSchemaException
    {
        final JsonNode node = factory.objectNode()
            .put("$ref", "http://foo.bar/a/b");

        final JsonRef ref1 = JsonRef.fromNode(node, "$ref");
        assertTrue(ref1.isNormalized());
    }

    @Test
    public void nonNormalizedURIsShouldBeIdentifiedAsSuch()
        throws JsonSchemaException
    {
        final JsonNode node = factory.objectNode()
            .put("$ref", "http://foo.bar/a/b/..");

        final JsonRef ref1 = JsonRef.fromNode(node, "$ref");
        assertFalse(ref1.isNormalized());
    }

    @Test
    public void afterURINormalizationJsonRefsShouldBeEqual()
        throws JsonSchemaException
    {
        final JsonNode node1, node2;

        node1 = factory.objectNode().put("$ref", "http://foo.bar/a/b");

        node2 = factory.objectNode().put("$ref", "http://foo.bar/c/../a/./b");

        final JsonRef ref1 = JsonRef.fromNode(node1, "$ref");
        final JsonRef ref2 = JsonRef.fromNode(node2, "$ref");
        assertEquals(ref1, ref2);
    }

    @Test
    public void absoluteRefsShouldBeIdentifiedAsSuch()
        throws JsonSchemaException
    {
        final JsonNode node1, node2;

        node1 = factory.objectNode().put("$ref", "http://foo.bar/a/b");

        node2 = factory.objectNode().put("$ref", "foo.bar");

        final JsonRef ref1 = JsonRef.fromNode(node1, "$ref");
        final JsonRef ref2 = JsonRef.fromNode(node2, "$ref");

        assertTrue(ref1.isAbsolute());
        assertFalse(ref2.isAbsolute());
    }

    @Test
    public void absoluteURIWithFragmentIsNotAnAbsoluteRef()
        throws JsonSchemaException
    {
        final JsonNode node = factory.objectNode()
            .put("$ref", "http://foo.bar/a/b#c");

        final JsonRef ref = JsonRef.fromNode(node, "$ref");

        assertFalse(ref.isAbsolute());
    }

    @Test
    public void testFragments()
        throws JsonSchemaException
    {
        final JsonNode node = factory.objectNode()
            .put("f1", "file:///a")
            .put("f2", "file:///a#")
            .put("f3", "file:///a#b/c");

        JsonRef ref;

        ref = JsonRef.fromNode(node, "f1");
        assertFalse(ref.hasFragment());

        ref = JsonRef.fromNode(node, "f2");
        assertFalse(ref.hasFragment());

        ref = JsonRef.fromNode(node, "f3");
        assertTrue(ref.hasFragment());
        assertEquals(ref.getFragment(), "b/c");
    }

    @Test
    public void testEmptyFragmentVsNoFragment()
        throws JsonSchemaException
    {
        final JsonNode node = factory.objectNode()
            .put("ref1", "http://foo.bar")
            .put("ref2", "http://foo.bar#");

        final JsonRef ref1 = JsonRef.fromNode(node, "ref1");
        final JsonRef ref2 = JsonRef.fromNode(node, "ref2");

        assertEquals(ref1, ref2);
    }
}
