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

package org.eel.kitchen.jsonschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.eel.kitchen.jsonschema.util.JsonProvider;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.*;

public final class GsonProviderTest
{
    private static final JsonProvider<JsonElement> provider
        = GsonProvider.getInstance();

    private JsonNode testData;

    @BeforeClass
    public void initData()
        throws IOException
    {
        testData = JsonLoader.fromResource("/provider/gson.json");
    }

    @DataProvider
    public Iterator<Object[]> getData()
    {
        final Set<Object[]> set = Sets.newHashSet();

        for (final JsonNode element: testData)
            set.add(new Object[] { element });

        return set.iterator();
    }

    @Test(dataProvider = "getData")
    public void testJacksonToGsonAndBack(final JsonNode jackson)
    {
        final JsonElement toGson = provider.fromJsonNode(jackson);
        final JsonNode andBack = provider.toJsonNode(toGson);

        assertEquals(jackson, andBack);
    }

    @DataProvider
    public Iterator<Object[]> getGson()
        throws IOException
    {
        final Set<Object[]> set = Sets.newHashSet();
        final Gson gson = new Gson();
        final InputStream in
            = getClass().getResourceAsStream("/provider/gson.json");

        final Reader reader = new InputStreamReader(in);
        try {
            // Unlike Jackson, Gson cannot read to its own format :/
            final JsonElement[] array
                = gson.fromJson(reader, JsonElement[].class);
            for (final JsonElement element: array)
                set.add(new Object[] { element });
            return set.iterator();
        } finally {
            reader.close();
            in.close();
        }
    }

    @Test(dataProvider = "getGson")
    public void testGsonToJacksonAndBack(final JsonElement gson)
    {
        final JsonNode toJackson = provider.toJsonNode(gson);
        final JsonElement andBack = provider.fromJsonNode(toJackson);

        assertEquals(gson, andBack);
    }
}
