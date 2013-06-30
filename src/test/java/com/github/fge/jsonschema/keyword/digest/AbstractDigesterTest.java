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

package com.github.fge.jsonschema.keyword.digest;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jackson.JsonNumEquals;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.library.Dictionary;
import com.google.common.base.Equivalence;
import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

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
