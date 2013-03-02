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

package com.github.fge.jsonschema.walk;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.JsonReferenceException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.JsonLoader;
import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

public abstract class AbstractPointerCollectorTest
{
    private final String keyword;
    private final PointerCollector collector;
    private final JsonNode testData;

    protected AbstractPointerCollectorTest(
        final Dictionary<PointerCollector> dict, final String prefix,
        final String keyword)
        throws IOException
    {
        this.keyword = keyword;
        final String resource = "/walk/" + prefix + '/' + keyword + ".json";
        testData = JsonLoader.fromResource(resource);
        collector = dict.entries().get(keyword);
    }

    @Test
    public final void keywordIsSupported()
    {
        assertNotNull(collector, keyword + " is not supported??");
    }

    @DataProvider
    public final Iterator<Object[]> getTestData()
        throws JsonReferenceException
    {
        final List<Object[]> list = Lists.newArrayList();

        JsonNode schema;
        List<JsonPointer> pointers;

        for (final JsonNode element: testData) {
            schema = element.get("schema");
            pointers = Lists.newArrayList();
            for (final JsonNode node: element.get("pointers"))
                pointers.add(new JsonPointer(node.textValue()));
            list.add(new Object[]{ schema, pointers });
        }

        return list.iterator();
    }

    @Test(dependsOnMethods = "keywordIsSupported", dataProvider = "getTestData")
    public final void pointersAreCorrectlyComputed(final JsonNode schema,
        final List<JsonPointer> pointers)
    {
        final SchemaTree tree = new CanonicalSchemaTree(schema);
        final List<JsonPointer> collected = Lists.newArrayList();

        collector.collect(collected, tree);

        assertEquals(collected, pointers,
            "pointer list differs from expectations");
    }
}

