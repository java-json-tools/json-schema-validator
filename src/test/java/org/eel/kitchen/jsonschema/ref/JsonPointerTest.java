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
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.main.ValidationDomain;
import org.eel.kitchen.jsonschema.main.ValidationMessage;
import org.eel.kitchen.jsonschema.util.JacksonUtils;
import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.*;

public final class JsonPointerTest
{
    private JsonNode document, pointerData, uriData;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        final JsonNode node = JsonLoader.fromResource("/ref/jsonpointer.json");
        document = node.get("document");
        pointerData = node.get("pointers");
        uriData = node.get("uris");
    }

    private static Iterator<Object[]> nodeToDataProvider(final JsonNode node)
    {
        final Map<String, JsonNode> map = JacksonUtils.nodeToMap(node);

        final Set<Object[]> set = new HashSet<Object[]>();

        for (final Map.Entry<String, JsonNode> entry: map.entrySet())
            set.add(new Object[] { entry.getKey(), entry.getValue() });

        return set.iterator();
    }

    @DataProvider
    public Iterator<Object[]> getPointerData()
    {
        return nodeToDataProvider(pointerData);
    }

    @Test(dataProvider = "getPointerData")
    public void pointersResolveCorrectly(final String input,
        final JsonNode expected)
        throws JsonSchemaException
    {
        final JsonPointer ptr = new JsonPointer(input);
        final JsonNode actual = ptr.resolve(document);

        assertEquals(actual, expected, "failed to resolve JSON Pointer "
            + input);
    }

    @DataProvider
    public Iterator<Object[]> getURIData()
    {
        return nodeToDataProvider(uriData);
    }

    @Test(dataProvider = "getURIData")
    public void URIEncodedPointersResolveCorrectly(final String input,
        final JsonNode expected)
        throws JsonSchemaException
    {
        final JsonPointer ptr
            = new JsonPointer(URI.create(input).getFragment());
        final JsonNode actual = ptr.resolve(document);

        assertEquals(actual, expected, "failed to resolve URI encoded JSON "
            + "Pointer " + input);
    }

    @DataProvider
    public Iterator<Object[]> getIllegalPointerData()
    {
        final Set<Object[]> set = new HashSet<Object[]>();

        String input, errmsg, message;

        input = "x";
        errmsg = "illegal JSON Pointer: reference token not preceeded by '/'";
        message = "pointer not starting with / should be illegal";
        set.add(new Object[] { input, errmsg, message });

        input = "/~";
        errmsg = "illegal JSON Pointer: bad escape sequence: ~ not followed " +
            "by any token";
        message = "~ without any following character should be illegal";
        set.add(new Object[] { input, errmsg, message });

        input = "/~x";
        errmsg = "illegal JSON Pointer: bad escape sequence: ~ should be " +
            "followed by one of [0, 1], but was followed by 'x'";
        message = "~ not followed by 0 or 1 should be illegal";
        set.add(new Object[] { input, errmsg, message });

        return set.iterator();
    }

    @Test(dataProvider = "getIllegalPointerData")
    public void illegalJSONPointerMustBeDetectedAsSuch(final String input,
        final String errmsg, final String message)
    {
        try {
            new JsonPointer(input);
            fail(message);
        } catch (JsonSchemaException e) {
            assertEquals(e.getMessage(), errmsg);
        }
    }

    private static void checkMessage(final ValidationMessage message,
        final JsonNode info)
    {
        assertSame(message.getDomain(), ValidationDomain.REF_RESOLVING);
        assertEquals(message.getKeyword(), "$ref");
        assertEquals(message.getMessage(), "illegal JSON Pointer");

        final Map<String, JsonNode> map = JacksonUtils.nodeToMap(info);

        String key;
        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            key = entry.getKey();
            assertEquals(message.getInfo(key), info.get(key));
        }
    }
}
