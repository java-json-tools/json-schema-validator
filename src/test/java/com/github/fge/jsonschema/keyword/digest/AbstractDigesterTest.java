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

package com.github.fge.jsonschema.keyword.digest;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jackson.JsonNumEquals;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.util.Dictionary;
import com.google.common.base.Equivalence;
import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

@Test
public abstract class AbstractDigesterTest
{
    private static final Equivalence<JsonNode> EQUIVALENCE
        = JsonNumEquals.getInstance();

    private final String keyword;
    private final Digester digester;
    private final EnumSet<NodeType> types;
    private final JsonNode data;

    protected AbstractDigesterTest(final Dictionary<Digester> dict,
        final String prefix, final String keyword, final NodeType first,
        final NodeType... other)
        throws IOException
    {
        digester = dict.entries().get(keyword);
        types = EnumSet.of(first, other);
        this.keyword = keyword;
        final String resourceName = String.format("/keyword/digest/%s/%s.json",
            prefix, keyword);
        data = JsonLoader.fromResource(resourceName);
    }

    @Test
    public final void keywordExists()
    {
        assertNotNull(digester, keyword + " is not supported??");
        assertEquals(digester.supportedTypes(), types,
            "keyword does not declare to support the appropriate type set");
    }

    @DataProvider
    public final Iterator<Object[]> getTestData()
    {
        final List<Object[]> list = Lists.newArrayList();

        JsonNode digest;
        for (final JsonNode element: data) {
            digest = element.get("digest");
            for (final JsonNode node: element.get("inputs"))
                list.add(new Object[] { digest, node });
        }

        return list.iterator();
    }

    @Test(dependsOnMethods = "keywordExists", dataProvider = "getTestData")
    public final void digestsAreCorrectlyComputed(final JsonNode digest,
        final JsonNode source)
    {
        assertTrue(EQUIVALENCE.equivalent(digester.digest(source), digest),
            "incorrect digest form");
    }
}
