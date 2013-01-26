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

package com.github.fge.jsonschema.ref;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.fge.jsonschema.main.JsonSchemaException;
import com.github.fge.jsonschema.report.Domain;
import com.github.fge.jsonschema.report.Message;
import com.github.fge.jsonschema.util.JsonLoader;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import com.google.common.collect.ImmutableSet;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.annotations.Sets;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.*;

public final class JsonPointerTest
{
    private JsonNode document, pointerData, uriData, illegal;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        final JsonNode node = JsonLoader.fromResource("/ref/jsonpointer.json");
        document = node.get("document");
        pointerData = node.get("pointers");
        uriData = node.get("uris");
        illegal = JsonLoader.fromResource("/ref/jsonpointer-illegal.json");
    }

    private static Iterator<Object[]> nodeToDataProvider(final JsonNode node)
    {
        final Map<String, JsonNode> map = JacksonUtils.asMap(node);

        final Set<Object[]> set = Sets.newHashSet();

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
        final Set<Object[]> set = Sets.newHashSet();

        for (final JsonNode node: illegal)
            set.add(mungeArguments(node));

        return set.iterator();
    }

    private static Object[] mungeArguments(final JsonNode node)
    {
        final Message.Builder msg = Domain.REF_RESOLVING.newMessage()
            .setKeyword("$ref").setMessage("illegal JSON Pointer");

        final Map<String, JsonNode> map
            = JacksonUtils.asMap(node.get("info"));

        for (final Map.Entry<String, JsonNode> entry: map.entrySet())
            msg.addInfo(entry.getKey(), entry.getValue());

        return new Object[] { node.get("input").textValue(), msg.build() };
    }

    @Test(dataProvider = "getIllegalPointerData")
    public void illegalJSONPointerMustBeDetectedAsSuch(final String input,
        final Message msg)
    {
        try {
            new JsonPointer(input);
            fail(input + " was supposed to be illegal");
        } catch (JsonSchemaException e) {
            assertEquals(e.getValidationMessage(), msg);
        }
    }

    @Test
    public void zeroPrefixedIntegersAreNotValidArrayPointers()
        throws JsonSchemaException
    {
        final JsonPointer ptr = new JsonPointer("/00");
        final ArrayNode node = JsonNodeFactory.instance.arrayNode()
            .add(false);

        assertTrue(ptr.resolve(node).isMissingNode());
    }

    @DataProvider
    public Iterator<Object[]> parentData()
    {
        return ImmutableSet.of(
            new Object[] { "", "", true },
            new Object[] { "/a", "", false },
            new Object[] { "/a", "/b", false },
            new Object[] { "/a", "/a/b", true },
            new Object[] { "/x/00/t", "/x/00/t/", true },
            new Object[] { "/a/b", "//a/b", false }
        ).iterator();
    }

    @Test(dataProvider = "parentData")
    public void parentRelationshipsAreCorrectlyDetected(final String s1,
        final String s2, final boolean isParent)
        throws JsonSchemaException
    {
        final JsonPointer ptr1 = new JsonPointer(s1);
        final JsonPointer ptr2 = new JsonPointer(s2);
        assertEquals(ptr1.isParentOf(ptr2), isParent);
    }
}
