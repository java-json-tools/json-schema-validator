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

package com.github.fge.jsonschema.processing.build;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.TestUtils;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.util.JsonLoader;
import com.google.common.base.Equivalence;
import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

public abstract class AbstractKeywordEquivalenceTest
{
    protected final String keyword;
    protected final Dictionary<KeywordDescriptor> dict;
    protected final JsonNode data;
    protected final KeywordDescriptor descriptor;
    protected final Equivalence<JsonNode> equivalence;

    protected AbstractKeywordEquivalenceTest(
        final Dictionary<KeywordDescriptor> dict, final String prefix,
        final String keyword)
        throws IOException
    {
        this.keyword = keyword;
        this.dict = dict;

        descriptor = dict.get(keyword);
        equivalence = descriptor == null ? null : descriptor.equivalence;

        final String resourceName
            = String.format("/keyword/equivalences/%s/%s.json", prefix, keyword);
        data = JsonLoader.fromResource(resourceName);
    }

    @Test
    public final void keywordExists()
    {
        assertNotNull(descriptor, "no support for " + keyword + "??");
    }

    @DataProvider
    public final Iterator<Object[]> getEquivalences()
    {
        final JsonNode equivalences = data.get("equivalences");
        final List<Object[]> list = Lists.newArrayList();

        List<List<JsonNode>> pairs;
        for (final JsonNode node: equivalences) {
            pairs = TestUtils.allPairs(Lists.newArrayList(node));
            for (final List<JsonNode> pair: pairs)
                list.add(new Object[] { pair.get(0), pair.get(1) });
        }


        return list.iterator();
    }

    @Test(dataProvider = "getEquivalences", dependsOnMethods = "keywordExists")
    public final void equivalencesAreCorrectlyComputed(final JsonNode a,
        final JsonNode b)
    {
        assertTrue(equivalence.equivalent(a, b),
            a + " was not considered equivalent to " + b);
    }

    @DataProvider
    public final Iterator<Object[]> getDifferences()
    {
        final JsonNode differences = data.get("differences");
        final List<List<JsonNode>> pairs
            = TestUtils.allPairs(Lists.newArrayList(differences));

        final List<Object[]> list = Lists.newArrayList();

        for (final List<JsonNode> pair: pairs)
            list.add(new Object[] { pair.get(0), pair.get(1) });

        return list.iterator();
    }

    @Test(dataProvider = "getDifferences", dependsOnMethods = "keywordExists")
    public final void differencesAreCorrectlyComputed(final JsonNode a,
        final JsonNode b)
    {
        assertFalse(equivalence.equivalent(a, b),
            a + " was considered equivalent to " + b);
    }
}
