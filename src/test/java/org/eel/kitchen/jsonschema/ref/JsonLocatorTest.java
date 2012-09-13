/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
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

package org.eel.kitchen.jsonschema.ref;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;
import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.assertEquals;

public final class JsonLocatorTest
{
    private JsonNode data;

    @BeforeClass
    public void initData()
        throws IOException
    {
        data = JsonLoader.fromResource("/ref/jsonlocator.json");
    }

    @DataProvider
    public Iterator<Object[]> getData()
    {
        final Set<Object[]> set = Sets.newHashSet();

        for (final JsonNode node: data)
            set.add(new Object[] {
                node.get("base").textValue(),
                node.get("against").textValue(),
                node.get("result").textValue()
            });

        return set.iterator();
    }

    @Test(dataProvider = "getData")
    public void locatorResolvingWorks(final String base, final String against,
        final String result)
    {
        final JsonLocator baseLocator = JsonLocator.fromURI(URI.create(base));
        final JsonLocator resolved = JsonLocator.fromURI(URI.create(against));
        final JsonLocator expected = JsonLocator.fromURI(URI.create(result));

        assertEquals(baseLocator.resolve(resolved), expected);

    }
}
