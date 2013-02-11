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

package com.github.fge.jsonschema.tree;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.github.fge.jsonschema.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.annotations.Sets;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.*;

public final class InlineSchemaTreeTest
{
    private SchemaTree schemaTree;
    private JsonNode lookups;

    @BeforeClass
    public void loadData()
        throws IOException
    {
        final JsonNode data = JsonLoader.fromResource("/tree/retrieval.json");
        lookups = data.get("lookups");

        final JsonNode schema = data.get("schema");
        schemaTree = new InlineSchemaTree(schema);
    }

    @DataProvider
    public Iterator<Object[]> getLookups()
        throws ProcessingException
    {
        final Set<Object[]> set = Sets.newHashSet();

        for (final JsonNode lookup: lookups)
            set.add(new Object[] {
                JsonRef.fromString(lookup.get("id").textValue()),
                new JsonPointer(lookup.get("ptr").textValue())
            });

        return set.iterator();
    }

    @Test(dataProvider = "getLookups")
    public void inlineSchemaTreeContainsDeclaredContext(final JsonRef ref,
        final JsonPointer ptr)
    {
        assertTrue(schemaTree.containsRef(ref));
        assertEquals(schemaTree.matchingPointer(ref), ptr);
    }

    @DataProvider
    public Iterator<Object[]> cornerCases()
    {
        final Set<Object[]> set = Sets.newHashSet();
        final JsonPointer empty = JsonPointer.empty();

        set.add(new Object[] { "foo#", "foo#a", false, null } );
        set.add(new Object[] { "foo#a", "foo#a", true, empty } );
        set.add(new Object[] { "foo#a", "foo#b", false, null } );
        set.add(new Object[] { "foo#a", "foo#a/b", false, null } );
        set.add(new Object[] { "foo#", "foo#/a", true, null } );
        set.add(new Object[] { "foo#/a", "foo#/a", true, empty } );
        set.add(new Object[] { "foo#/a/b", "foo#/a", false, null } );
        set.add(new Object[] { "foo#/a/b", "foo#/a/c", false, null } );
        set.add(new Object[] { "foo#/a/b", "foo#/a/b", true, empty } );
        set.add(new Object[] { "foo#/a/b", "foo#/a/b/c", true, null } );

        return set.iterator();
    }

    @Test(dataProvider = "cornerCases")
    public void cornerCasesAreHandledCorrectly(final String id,
        final String refAsString, final boolean resolvable,
        final JsonPointer ptr)
        throws ProcessingException
    {
        final JsonNodeFactory factory = JacksonUtils.nodeFactory();
        final ObjectNode schema = factory.objectNode();

        JsonRef ref;

        ref = JsonRef.fromString(id);
        schema.put("id", factory.textNode(ref.toString()));
        final SchemaTree tree = new InlineSchemaTree(schema);

        ref = JsonRef.fromString(refAsString);
        assertEquals(tree.containsRef(ref), resolvable);
        assertEquals(tree.matchingPointer(ref), ptr);
    }

}
