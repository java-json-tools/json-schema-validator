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
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.ref.JsonRef;
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
}
