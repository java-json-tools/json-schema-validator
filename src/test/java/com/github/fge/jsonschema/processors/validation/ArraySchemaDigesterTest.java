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
import com.github.fge.jsonschema.keyword.digest.Digester;
import com.github.fge.jsonschema.util.JsonLoader;
import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

public final class ArraySchemaDigesterTest
{
    private final Digester digester = ArraySchemaDigester.getInstance();
    private final JsonNode testNode;

    public ArraySchemaDigesterTest()
        throws IOException
    {
        testNode = JsonLoader.fromResource("/array/digest.json");
    }

    @DataProvider
    public Iterator<Object[]> testData()
    {
        JsonNode digest;
        final List<Object[]> list = Lists.newArrayList();

        for (final JsonNode node: testNode) {
            digest = node.get("digest");
            for (final JsonNode input: node.get("inputs"))
                list.add(new Object[] { digest, input });
        }

        return list.iterator();
    }

    @Test(dataProvider = "testData")
    public void digestsAreCorrectlyComputed(final JsonNode digest,
        final JsonNode input)
    {
        assertEquals(digester.digest(input), digest,
            "digested form is incorrect");
    }
}
