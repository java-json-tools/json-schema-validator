/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.jsonschema.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.fge.jsonschema.main.JsonSchemaException;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.util.JacksonUtils;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public final class SchemaContextTest
{
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

    @Test
    public void shouldConsiderRelativeIdAsAnonymousSchema()
        throws JsonSchemaException
    {
        final JsonNode node = FACTORY.objectNode().put("id", "foo");
        final SchemaContext schemaContext
            = AddressingMode.CANONICAL.forSchema(node);
        assertSame(schemaContext.getLocator(), JsonRef.emptyRef());
    }

    @Test
    public void twoContainersBuiltFromTheSameInputAreEqual()
        throws JsonSchemaException
    {
        final JsonNode n1 = FACTORY.objectNode().put("id", "a://b/c#");
        final JsonNode n2 = FACTORY.objectNode().put("id", "a://b/c#");

        final SchemaContext c1 = AddressingMode.CANONICAL.forSchema(n1);
        final SchemaContext c2 = AddressingMode.CANONICAL.forSchema(n2);

        assertEquals(c1.getLocator(), c2.getLocator());
        assertEquals(c1.getSchema(), c2.getSchema());
    }

    @Test
    public void noFragmentOrEmptyFragmentIsTheSame()
        throws JsonSchemaException
    {
        final JsonNode n1 = FACTORY.objectNode().put("id", "a://c");
        final JsonNode n2 = FACTORY.objectNode().put("id", "a://c#");

        final SchemaContext c1 = AddressingMode.CANONICAL.forSchema(n1);
        final SchemaContext c2 = AddressingMode.CANONICAL.forSchema(n2);

        assertEquals(c1.getLocator(), c2.getLocator());
        assertEquals(c1.getSchema(), c2.getSchema());
    }

    @Test
    public void anonymousContextsWithDifferentSchemasShouldNotBeEqual()
    {
        final JsonNode schema1 = FACTORY.objectNode().put("a", "b");
        final JsonNode schema2 = FACTORY.objectNode().put("c", "d");

        final SchemaContext c1 = AddressingMode.CANONICAL.forSchema(schema1);
        final SchemaContext c2 = AddressingMode.CANONICAL.forSchema(schema2);

        assertFalse(c1.equals(c2));
    }
}
