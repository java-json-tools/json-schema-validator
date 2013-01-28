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
import com.github.fge.jsonschema.processing.ProcessingMessage;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.github.fge.jsonschema.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.annotations.Sets;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.*;

public final class RefResolverProcessorTest
{
    private JsonNode loops;
    private JsonNode dangling;
    private SchemaLoader loader;
    private RefResolverProcessor processor;

    @BeforeClass
    public void loadFiles()
        throws IOException
    {
        loops = JsonLoader.fromResource("/processing/ref/canonical/loops.json");
        dangling = JsonLoader.fromResource("/processing/ref/canonical"
            + "/dangling.json");
        loader = new SchemaLoader(new URIManager(), URI.create(""), false);
        processor = new RefResolverProcessor(loader);
    }

    @DataProvider
    public Iterator<Object[]> getLoops()
        throws JsonSchemaException
    {
        final Set<Object[]> set = Sets.newHashSet();

        for (final JsonNode element: loops)
            set.add(new Object[] {
                element.get("schema"),
                new JsonPointer(element.get("pointer").textValue()),
                element.get("ref").textValue(),
                element.get("path")
            });

        return set.iterator();
    }

    @Test(dataProvider = "getLoops")
    public void refLoopsAreDetected(final JsonNode schema,
        final JsonPointer pointer, final String ref, final JsonNode pathNode)
    {
        final JsonSchemaTree tree = loader.load(schema);
        tree.setPointer(pointer);
        final JsonSchemaContext context = new JsonSchemaContext(tree);

        final JsonNode node;

        try {
            processor.process(context);
            fail("No exception thrown!");
        } catch (ProcessingException e) {
            final ProcessingMessage msg = e.getProcessingMessage();
            node = msg.asJson();
            assertEquals(node.get("message").textValue(),
                "JSON Reference loop detected");
            assertEquals(node.get("ref").textValue(), ref);
            assertEquals(node.get("path"), pathNode);
        }
    }

    @DataProvider
    public Iterator<Object[]> getDangling()
        throws JsonSchemaException
    {
        final Set<Object[]> set = Sets.newHashSet();

        for (final JsonNode element: dangling)
            set.add(new Object[] {
                element.get("schema"),
                new JsonPointer(element.get("pointer").textValue()),
                element.get("ref").textValue()
            });

        return set.iterator();
    }

    @Test(dataProvider = "getDangling")
    public void danglingRefsAreDetected(final JsonNode schema,
        final JsonPointer pointer, final String ref)
    {
        final JsonSchemaTree tree = loader.load(schema);
        tree.setPointer(pointer);
        final JsonSchemaContext context = new JsonSchemaContext(tree);

        final JsonNode node;

        try {
            processor.process(context);
            fail("No exception thrown!");
        } catch (ProcessingException e) {
            final ProcessingMessage msg = e.getProcessingMessage();
            node = msg.asJson();
            assertEquals(node.get("message").textValue(),
                "unresolvable JSON Reference");
            assertEquals(node.get("ref").textValue(), ref);
        }
    }
}
