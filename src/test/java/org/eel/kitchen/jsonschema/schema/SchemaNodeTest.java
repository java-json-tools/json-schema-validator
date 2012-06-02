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
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public final class SchemaNodeTest
{
    private final JsonNodeFactory factory = JsonNodeFactory.instance;

    @Test
    public void equalsIsSymmetricAndReflexive()
        throws JsonSchemaException
    {
        final JsonNode node = factory.objectNode();
        final SchemaContainer container = new SchemaContainer(node);
        final SchemaNode n1 = new SchemaNode(container, node);
        final SchemaNode n2 = new SchemaNode(container, node);

        assertTrue(n1.equals(n1));
        assertTrue(n1.equals(n2));
        assertTrue(n2.equals(n1));
        assertEquals(n1.hashCode(), n2.hashCode());
    }

    @Test
    public void equalsIsTransitive()
        throws JsonSchemaException
    {
        final JsonNode node = factory.objectNode();
        final SchemaContainer container = new SchemaContainer(node);
        final SchemaNode n1 = new SchemaNode(container, node);
        final SchemaNode n2 = new SchemaNode(container, node);
        final SchemaNode n3 = new SchemaNode(container, node);

        assertTrue(n1.equals(n2));
        assertTrue(n2.equals(n3));
        assertTrue(n1.equals(n3));
    }
}
