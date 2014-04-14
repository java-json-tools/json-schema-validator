/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.processors.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jackson.jsonpointer.JsonPointerException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
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
        throws ProcessingException, JsonPointerException
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
