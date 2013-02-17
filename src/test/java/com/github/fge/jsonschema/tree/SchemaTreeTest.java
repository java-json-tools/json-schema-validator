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
import com.github.fge.jsonschema.SampleNodeProvider;
import com.github.fge.jsonschema.exceptions.JsonReferenceException;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.github.fge.jsonschema.util.JsonLoader;
import com.github.fge.jsonschema.util.NodeType;
import com.google.common.collect.ImmutableList;
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

public final class SchemaTreeTest
{
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

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
        SchemaTree schemaTree;

        schemaTree = new CanonicalSchemaTree(factory.objectNode());
        assertSame(schemaTree.getContext(), JsonRef.emptyRef());

        final URI uri = URI.create("foo://bar");
        final JsonRef ref = JsonRef.fromURI(uri);

        schemaTree = new CanonicalSchemaTree(ref, factory.objectNode());
        assertSame(schemaTree.getContext(), ref);
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
        throws ProcessingException
    {
        final JsonRef loadingRef = JsonRef.fromString(loading);
        final JsonRef idRef = JsonRef.fromString(id);
        final JsonRef resolved = loadingRef.resolve(idRef);

        final ObjectNode node = factory.objectNode();
        node.put("id", id);

        final SchemaTree tree = new CanonicalSchemaTree(loadingRef, node);
        assertEquals(tree.getContext(), resolved);
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
        throws ProcessingException
    {
        final JsonPointer ptr = new JsonPointer(path);
        final JsonRef scope = JsonRef.fromString(s);
        final SchemaTree tree = new CanonicalSchemaTree(schema);

        assertEquals(tree.append(ptr).getContext(), scope);
    }

    @Test(dataProvider = "getContexts")
    public void pointerSetCorrectlyCalculatesContext(final String path,
        final String s)
        throws ProcessingException
    {
        final JsonPointer ptr = new JsonPointer(path);
        final JsonRef scope = JsonRef.fromString(s);
        SchemaTree tree = new CanonicalSchemaTree(schema);
        final JsonRef origRef = tree.getContext();

        tree = tree.setPointer(ptr);
        assertEquals(tree.getContext(), scope);
        tree = tree.setPointer(JsonPointer.empty());
        assertEquals(tree.getContext(), origRef);
    }

    @DataProvider
    public Iterator<Object[]> nonSchemas()
    {
        return SampleNodeProvider.getSamplesExcept(NodeType.OBJECT);
    }

    @Test(dataProvider = "nonSchemas")
    public void nonSchemasYieldAnEmptyRef(final JsonNode node)
    {
        final SchemaTree tree = new CanonicalSchemaTree(node);
        assertEquals(tree.getDollarSchema(), JsonRef.emptyRef());
    }

    @DataProvider
    public Iterator<Object[]> nonStringDollarSchemas()
    {
        return SampleNodeProvider.getSamples(NodeType.STRING);
    }

    @Test
    public void schemaWithoutDollarSchemaYieldsAnEmptyRef()
    {
        final SchemaTree tree = new CanonicalSchemaTree(FACTORY.objectNode());
        assertEquals(tree.getDollarSchema(), JsonRef.emptyRef());
    }

    @Test(dataProvider = "nonStringDollarSchemas")
    public void nonTextualDollarSchemasYieldAnEmptyRef(final JsonNode node)
    {
        final ObjectNode testNode = FACTORY.objectNode();
        testNode.put("$schema", node);

        final SchemaTree tree = new CanonicalSchemaTree(testNode);
        assertEquals(tree.getDollarSchema(), JsonRef.emptyRef());
    }

    @DataProvider
    public Iterator<Object[]> nonLegalDollarSchemas()
    {
        return ImmutableList.of(
            new Object[] { "" },
            new Object[] { "foo#" },
            new Object[] { "http://my.site/myschema#a" }
        ).iterator();
    }

    @Test(dataProvider = "nonLegalDollarSchemas")
    public void nonAbsoluteDollarSchemasYieldAnEmptyRef(final String s)
    {
        final ObjectNode testNode = FACTORY.objectNode();
        testNode.put("$schema", FACTORY.textNode(s));

        final SchemaTree tree = new CanonicalSchemaTree(testNode);
        assertEquals(tree.getDollarSchema(), JsonRef.emptyRef());
    }

    @DataProvider
    public Iterator<Object[]> legalDollarSchemas()
    {
        return ImmutableList.of(
            new Object[] { "http://json-schema.org/schema#" },
            new Object[] { "http://json-schema.org/draft-03/schema" },
            new Object[] { "http://json-schema.org/draft-04/schema#" },
            new Object[] { "http://me.org/myschema" }
        ).iterator();
    }

    @Test(dataProvider = "legalDollarSchemas")
    public void legalDollarSchemasAreReturnedCorrectly(final String s)
        throws JsonReferenceException
    {
        final JsonRef ref = JsonRef.fromString(s);
        final ObjectNode testNode = FACTORY.objectNode();
        testNode.put("$schema", FACTORY.textNode(s));

        final SchemaTree tree = new CanonicalSchemaTree(testNode);
        assertEquals(tree.getDollarSchema(), ref);
    }
}
