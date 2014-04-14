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
import com.github.fge.jackson.JsonNumEquals;
import com.github.fge.jsonschema.keyword.digest.Digester;
import com.google.common.base.Equivalence;
import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

public final class ObjectSchemaDigesterTest
{
    private static final Equivalence<JsonNode> EQUIVALENCE
        = JsonNumEquals.getInstance();

    private final Digester digester = ObjectSchemaDigester.getInstance();
    private final JsonNode testNode;

    public ObjectSchemaDigesterTest()
        throws IOException
    {
        testNode = JsonLoader.fromResource("/object/digest.json");
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
        assertTrue(EQUIVALENCE.equivalent(digester.digest(input), digest),
            "digested form is incorrect");
    }
}
