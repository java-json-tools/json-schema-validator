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
import com.github.fge.jsonschema.main.JsonSchemaException;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.github.fge.jsonschema.util.JsonLoader;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.*;

public final class JsonSchemaTreeTest
{
    /*
     * This class only tests that the context information is calculated
     * correctly
     */
    private final JsonNodeFactory factory = JacksonUtils.nodeFactory();
    private JsonNode data;
    private JsonNode schema;

    @BeforeClass
    public void init()
        throws IOException
    {
        data = JsonLoader.fromResource("/tree/context.json");
        schema = data.get("schema");
    }

    @Test
    public void loadingRefIsReturnedWhenNoIdAtTopLevel()
    {
        JsonSchemaTree schemaTree;

        schemaTree = new CanonicalSchemaTree(factory.objectNode());
        assertSame(schemaTree.getCurrentRef(), JsonRef.emptyRef());

        final URI uri = URI.create("foo://bar");
        final JsonRef ref = JsonRef.fromURI(uri);

        schemaTree = new CanonicalSchemaTree(ref, factory.objectNode());
        assertSame(schemaTree.getCurrentRef(), ref);
    }

    @DataProvider
    public Iterator<Object[]> sampleIds()
    {
        return ImmutableSet.of(
            new Object[] { "", "http://foo.bar" },
            new Object[] { "http://foo.bar/baz", "meh#la" },
            new Object[] { "ftp://ftp.lip6.fr/schema", "x://y" }
        ).iterator();
    }

    @Test(dataProvider = "sampleIds")
    public void topMostIdIsResolvedAgainstLoadingRef(final String loading,
        final String id)
        throws JsonSchemaException
    {
        final JsonRef loadingRef = JsonRef.fromString(loading);
        final JsonRef idRef = JsonRef.fromString(id);
        final JsonRef resolved = loadingRef.resolve(idRef);

        final ObjectNode node = factory.objectNode();
        node.put("id", id);

        final JsonSchemaTree tree = new CanonicalSchemaTree(loadingRef, node);
        assertEquals(tree.getCurrentRef(), resolved);
    }

    @DataProvider
    public Iterator<Object[]> getContexts()
    {
        final JsonNode node = data.get("lookups");

        final Set<Object[]> set = Sets.newHashSet();

        for (final JsonNode element: node)
            set.add(new Object[] {
                element.get("pointer").textValue(),
                element.get("scope").textValue()
            });

        return set.iterator();
    }

    @Test(dataProvider = "getContexts")
    public void pointerAppendCorrectlyCalculatesContext(final String path,
        final String s)
        throws JsonSchemaException
    {
        final JsonPointer ptr = new JsonPointer(path);
        final JsonRef scope = JsonRef.fromString(s);
        final JsonSchemaTree tree = new CanonicalSchemaTree(schema);
        final JsonRef origRef = tree.getCurrentRef();

        tree.append(ptr);
        assertEquals(tree.getCurrentRef(), scope);
        tree.pop();
        assertSame(tree.getCurrentRef(), origRef);
    }

    @Test(dataProvider = "getContexts")
    public void pointerSetCorrectlyCalculatesContext(final String path,
        final String s)
        throws JsonSchemaException
    {
        final JsonPointer ptr = new JsonPointer(path);
        final JsonRef scope = JsonRef.fromString(s);
        final JsonSchemaTree tree = new CanonicalSchemaTree(schema);
        final JsonRef origRef = tree.getCurrentRef();

        tree.setPointer(ptr);
        assertEquals(tree.getCurrentRef(), scope);
        tree.setPointer(JsonPointer.empty());
        assertSame(tree.getCurrentRef(), origRef);
    }
}
