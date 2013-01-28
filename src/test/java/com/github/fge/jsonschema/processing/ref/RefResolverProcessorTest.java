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

package com.github.fge.jsonschema.processing.ref;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.main.JsonSchemaException;
import com.github.fge.jsonschema.processing.JsonSchemaContext;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.InlineSchemaTree;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.github.fge.jsonschema.util.JsonLoader;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

public abstract class RefResolverProcessorTest
{
    private static final String RESOURCE = "/processing/ref/repo/schema1.json";
    private static final JsonRef LOADING_REF;

    static {
        try {
            LOADING_REF = JsonRef.fromString("resource:" + RESOURCE);
        } catch (JsonSchemaException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final JsonNode baseSchema;
    private final Dereferencing dereferencing;
    protected final RefResolverProcessor processor;

    protected RefResolverProcessorTest(final Dereferencing dereferencing)
        throws IOException
    {
        final SchemaLoader loader = new SchemaLoader(new URIManager(),
            URI.create("#"), dereferencing.useInline);
        this.dereferencing = dereferencing;
        processor = new RefResolverProcessor(loader);
        baseSchema = JsonLoader.fromResource(RESOURCE);
    }

    @DataProvider
    protected final Iterator<Object[]> loopData()
        throws JsonSchemaException
    {
        final JsonPointer basePtr = JsonPointer.empty()
            .append(dereferencing.name).append("loops");
        final JsonNode data = basePtr.resolve(baseSchema);
        final Map<String, JsonNode> map = JacksonUtils.asMap(data);
        final List<Object[]> list = Lists.newArrayList();

        JsonSchemaTree tree;
        JsonPointer ptr;
        JsonNode node, ref, path;
        String name;

        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            name = entry.getKey();
            node = entry.getValue();
            ptr = basePtr.append(name);
            tree = dereferencing.newTree(LOADING_REF, baseSchema);
            tree.append(ptr);
            ref = node.get("ref");
            path = node.get("path");
            list.add(new Object[] { tree, ref, path } );
        }

        return list.iterator();
    }

    @Test(dataProvider = "loopData")
    public final void referenceLoopsAreCorrectlyReported(
        final JsonSchemaTree tree, final JsonNode ref, final JsonNode path)
    {
        final JsonSchemaContext context = new JsonSchemaContext(tree);
        final JsonNode msgNode;

        try {
            processor.process(context);
            fail("No exception thrown!");
        } catch (ProcessingException e) {
            msgNode = e.getProcessingMessage().asJson();
            assertEquals(msgNode.get("message").textValue(),
                "JSON Reference loop detected");
            assertEquals(msgNode.get("ref"), ref);
            assertEquals(msgNode.get("path"), path);
        }
    }

    public enum Dereferencing
    {
        CANONICAL("canonical", false)
        {
            @Override
            JsonSchemaTree newTree(final JsonRef ref, final JsonNode node)
            {
                return new CanonicalSchemaTree(ref, node);
            }
        },
        INLINE("inline", true)
        {
            @Override
            JsonSchemaTree newTree(final JsonRef ref, final JsonNode node)
            {
                return new InlineSchemaTree(ref, node);
            }
        };

        private final String name;
        private final boolean useInline;

        abstract JsonSchemaTree newTree(final JsonRef ref, final JsonNode node);

        Dereferencing(final String name, final boolean useInline)
        {
            this.name = name;
            this.useInline = useInline;
        }
    }
}
