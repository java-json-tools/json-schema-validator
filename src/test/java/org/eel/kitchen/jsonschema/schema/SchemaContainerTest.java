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
import org.eel.kitchen.jsonschema.schema.SchemaContainer;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public final class SchemaContainerTest
{
    private static final JsonNodeFactory factory = JsonNodeFactory.instance;
    private JsonNode node;
    private SchemaContainer container;

    @Test
    public void shouldConsiderRelativeIdAsInvalid()
    {
        node = factory.objectNode().put("id", "foo");
        try {
            container = new SchemaContainer(node);
            fail("No exception thrown!");
        } catch (JsonSchemaException e) {
            assertEquals(e.getMessage(), "a parent schema's id must be "
                + "absolute");
        }
    }

    @Test
    public void shouldCorrectlyIdentifyContainedAbsoluteRef()
        throws JsonSchemaException
    {
        final String str = "http://foo.bar/baz#";
        final JsonRef ref = createRef(str);

        node = factory.objectNode().put("id", str);
        container = new SchemaContainer(node);

        assertTrue(container.contains(ref));
    }

    @Test
    public void shouldCorrectlyIdentifyNonContainedAbsoluteRef()
        throws JsonSchemaException
    {
        final String str1 = "http://foo.bar/baz#";
        final String str2 = "http://foo.bar/blah#";
        final JsonRef ref = createRef(str2);

        node = factory.objectNode().put("id", str1);
        container = new SchemaContainer(node);

        assertFalse(container.contains(ref));
    }

    @Test
    public void shouldCorrectlyIdentifyFragmentOnlyRef()
        throws JsonSchemaException
    {
        final String str = "http://foo.bar/baz#";
        final JsonRef ref = createRef("#someId");

        node = factory.objectNode().put("id", str);
        container = new SchemaContainer(node);

        assertTrue(container.contains(ref));
    }

    @Test
    public void shouldCorrectlyIdentifyContainedRelativeRefs()
        throws JsonSchemaException
    {
        final String str = "http://foo.bar/baz#";
        final JsonRef ref2 = createRef("baz");

        node = factory.objectNode().put("id", str);
        container = new SchemaContainer(node);

        assertTrue(container.contains(ref2));
    }

    @Test
    public void shouldCorrectlyIdentifyNonContainedRelativeRefs()
        throws JsonSchemaException
    {
        final String str = "http://foo.bar/baz#";
        final JsonRef ref2 = createRef("bar");

        node = factory.objectNode().put("id", str);
        container = new SchemaContainer(node);

        assertFalse(container.contains(ref2));
    }

    @Test
    public void shouldRefuseToBuildWithNonNormalizedID()
    {
        node = factory.objectNode()
            .put("id", "http://foo.bar/a/../b");

        try {
            new SchemaContainer(node);
            fail("No exception thrown!");
        } catch (JsonSchemaException e) {
            assertEquals(e.getMessage(), "a parent schema's id must be "
                + "normalized");
        }
    }

    @Test
    public void twoContainersBuiltFromTheSameInputAreEqual()
        throws JsonSchemaException
    {
        final JsonNode n1 = factory.objectNode().put("id", "a://b/c#");
        final JsonNode n2 = factory.objectNode().put("id", "a://b/c#");

        final SchemaContainer c1 = new SchemaContainer(n1);
        final SchemaContainer c2 = new SchemaContainer(n2);

        assertTrue(c1.equals(c2));
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void equalsShouldBeReflexive()
        throws JsonSchemaException
    {
        node = factory.objectNode().put("id", "a://b/c#");

        container = new SchemaContainer(node);

        assertTrue(container.equals(container));
    }

    @Test
    public void equalsShouldBeSymmetric()
        throws JsonSchemaException
    {
        node = factory.objectNode().put("id", "a://b/c#");

        final SchemaContainer c1 = new SchemaContainer(node);
        final SchemaContainer c2 = new SchemaContainer(node);

        // a => b is equivalent to !a || b
        assertTrue(!c1.equals(c2) || c2.equals(c1));
    }

    @Test
    public void equalsShouldBeTransitive()
        throws JsonSchemaException
    {
        node = factory.objectNode().put("id", "a://b/c#");

        final SchemaContainer c1 = new SchemaContainer(node);
        final SchemaContainer c2 = new SchemaContainer(node);
        final SchemaContainer c3 = new SchemaContainer(node);

        assertTrue(c1.equals(c2) && c2.equals(c3));
    }

    @Test
    public void noFragmentOrEmptyFragmentIsTheSame()
        throws JsonSchemaException
    {
        final JsonNode n1 = factory.objectNode().put("id", "a://c");
        final JsonNode n2 = factory.objectNode().put("id", "a://c#");

        final SchemaContainer c1 = new SchemaContainer(n1);
        final SchemaContainer c2 = new SchemaContainer(n2);

        assertTrue(c1.equals(c2));
    }

    private static JsonRef createRef(final String s)
        throws JsonSchemaException
    {
        final JsonNode node = factory.objectNode().put("x", s);
        return JsonRef.fromNode(node, "x");
    }
}
