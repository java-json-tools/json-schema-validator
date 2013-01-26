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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.main.JsonSchemaException;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.util.JsonLoader;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import com.google.common.collect.ImmutableSet;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.annotations.Sets;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.*;

public final class JsonSchemaTreeTest
{
    private final JsonNodeFactory factory = JacksonUtils.nodeFactory();
    private JsonNode data;
    private JsonNode schema;
    private JsonNode data2;
    private JsonNode schema2;

    @BeforeClass
    public void init()
        throws IOException
    {
        data = JsonLoader.fromResource("/tree/tree.json");
        schema = data.get("schema");
        data2 = JsonLoader.fromResource("/tree/retrieval.json");
        schema2 = data2.get("schema");
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
    public void contextsAreCorrectlyComputed(final String path, final String s)
        throws JsonSchemaException
    {
        final JsonPointer ptr = new JsonPointer(path);
        final JsonRef scope = JsonRef.fromString(s);
        final JsonSchemaTree tree = new CanonicalSchemaTree(schema);
        final JsonRef origRef = tree.getCurrentRef();

        tree.pushd(ptr);
        assertEquals(tree.getCurrentRef(), scope);
        tree.popd();
        assertSame(tree.getCurrentRef(), origRef);
    }

    @Test
    public void pathElementPushCalculatesContext()
        throws JsonSchemaException
    {
        final ObjectNode node = factory.objectNode();
        final ObjectNode child = factory.objectNode();
        final String id = "foo#";
        final JsonRef ref = JsonRef.fromString(id);
        child.put("id", id);
        node.put("child", child);

        final JsonSchemaTree tree = new CanonicalSchemaTree(node);
        tree.pushd("child");
        assertEquals(tree.getCurrentRef(), ref);
    }

    @Test
    public void arrayIndexPushCalcuatesContext()
        throws JsonSchemaException
    {
        final ArrayNode node = factory.arrayNode();
        final ObjectNode child = factory.objectNode();
        final String id = "foo#";
        final JsonRef ref = JsonRef.fromString(id);
        child.put("id", id);
        node.add(child);

        final JsonSchemaTree tree = new CanonicalSchemaTree(node);
        tree.pushd(0);
        assertEquals(tree.getCurrentRef(), ref);
    }

    @Test(dataProvider = "getContexts")
    public void canonicalSchemaTreeContainsNoInlineContexts(final String path,
        final String s)
        throws JsonSchemaException
    {
        final JsonRef loadingRef = JsonRef.fromString("foo://bar");
        final JsonRef scope = JsonRef.fromString(s);
        final JsonSchemaTree tree = new CanonicalSchemaTree(loadingRef, schema);

        assertTrue(tree.contains(loadingRef));
        assertFalse(tree.contains(scope));
    }

    @Test(dataProvider = "getContexts")
    public void inlineSchemaTreeContainsInlineContexts(final String path,
        final String s)
        throws JsonSchemaException
    {
        final JsonRef loadingRef = JsonRef.fromString("foo://bar");
        final JsonRef scope = JsonRef.fromString(s);
        final JsonSchemaTree tree = new InlineSchemaTree(loadingRef, schema);

        assertTrue(tree.contains(loadingRef));
        assertTrue(tree.contains(scope));
    }

    @DataProvider
    public Iterator<Object[]> retrievalData()
        throws JsonSchemaException
    {
        final JsonNode node = data2.get("retrievals");
        final Set<Object[]> set = Sets.newHashSet();

        for (final JsonNode element: node)
            set.add(new Object[] {
                JsonRef.fromString(element.get("id").textValue()),
                new JsonPointer(element.get("ptr").textValue())
            });

        return set.iterator();
    }

    @Test(dataProvider = "retrievalData")
    public void canonicalTreeRetrievesDataCorrectly(final JsonRef id,
        final JsonPointer ptr)
        throws URISyntaxException
    {
        final URI baseUri = new URI("x", "y", "/z", null);
        final JsonRef baseRef = JsonRef.fromURI(baseUri);
        final URI uri = new URI("x", "y", "/z", ptr.toString());
        final JsonRef ref = JsonRef.fromURI(uri);
        final JsonSchemaTree tree = new CanonicalSchemaTree(baseRef, schema2);

        final JsonNode expected = ptr.resolve(schema2);

        assertEquals(tree.retrieve(ref), expected);
    }

    @Test(dataProvider = "retrievalData")
    public void inlineTreeRetrievesDataCorrectly(final JsonRef id,
        final JsonPointer ptr)
        throws URISyntaxException
    {
        final URI baseUri = new URI("x", "y", "/z", null);
        final JsonRef baseRef = JsonRef.fromURI(baseUri);
        final JsonSchemaTree tree = new InlineSchemaTree(baseRef, schema2);

        final JsonNode expected = ptr.resolve(schema2);

        assertEquals(tree.retrieve(id), expected);
    }
}
