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

package com.github.fge.jsonschema.ref;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.util.JsonLoader;
import com.google.common.collect.ImmutableSet;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Iterator;

import static org.testng.Assert.*;

public final class JsonPointerLookupTest
{
    private JsonNode schema;

    @BeforeClass
    public void loadSchema()
        throws IOException
    {
        schema = JsonLoader.fromResource("/schema-lookup.json");
    }

    @DataProvider
    public Iterator<Object[]> getData()
    {
        String input, errmsg;
        JsonNode expected;

        final ImmutableSet.Builder<Object[]> builder = ImmutableSet.builder();

        /*
         * Empty fragments
         */
        expected = schema;

        input = "";
        errmsg = "empty pointer does not resolve to self";
        builder.add(new Object[] { input, expected, errmsg });

        /*
         * JSON Pointers -- existing and non existing
         */
        errmsg = "existing JSON Pointer lookup failed";

        input = "/properties";
        expected = schema.get("properties");
        builder.add(new Object[] { input, expected, errmsg });

        input = "/properties/additionalProperties/type/1";
        expected = schema.get("properties").get("additionalProperties")
            .get("type").get(1);
        builder.add(new Object[] { input, expected, errmsg });

        return builder.build().iterator();
    }

    @Test(dataProvider = "getData")
    public void pointerLookupSucceed(final String input,
        final JsonNode expected, final String errmsg)
        throws ProcessingException
    {
        final JsonPointer ptr = new JsonPointer(input);

        final JsonNode result = ptr.resolve(schema);

        assertEquals(result, expected, errmsg);
    }
}
