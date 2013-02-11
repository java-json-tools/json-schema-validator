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

package com.github.fge.jsonschema.processors.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.util.JsonLoader;
import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

public final class ArraySchemaSelectorTest
{
    private final JsonNode testNode;

    public ArraySchemaSelectorTest()
        throws IOException
    {
        testNode = JsonLoader.fromResource("/array/lookup.json");
    }

    @DataProvider
    public Iterator<Object[]> testData()
        throws ProcessingException
    {
        final List<Object[]> list = Lists.newArrayList();

        JsonNode digest;
        int elementIndex;
        List<JsonPointer> ret;
        for (final JsonNode node: testNode) {
            digest = node.get("digest");
            elementIndex = node.get("elementIndex").intValue();
            ret = Lists.newArrayList();
            for (final JsonNode element: node.get("ret"))
                ret.add(new JsonPointer(element.textValue()));
            list.add(new Object[]{ digest, elementIndex, ret });
        }

        return list.iterator();
    }

    @Test(dataProvider = "testData")
    public void schemaPointersAreCorrectlyComputed(final JsonNode digest,
        final int elementIndex, final List<JsonPointer> ret)
    {
        final ArraySchemaSelector selector = new ArraySchemaSelector(digest);
        final List<JsonPointer> actual
            = Lists.newArrayList(selector.selectSchemas(elementIndex));
        assertEquals(actual, ret, "schema lookup differs from expectations");
    }
}
