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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.main.SchemaRegistry;
import org.eel.kitchen.jsonschema.schema.SchemaContainer;
import org.eel.kitchen.jsonschema.schema.SchemaNode;
import org.eel.kitchen.jsonschema.uri.URIManager;
import org.eel.kitchen.util.CollectionUtils;
import org.eel.kitchen.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class JsonResolverAbsoluteURITest
{
    private JsonNode testData;
    private URIManager manager;
    private SchemaRegistry registry;
    private JsonResolver resolver;

    @BeforeClass
    public void setUp()
        throws IOException, JsonSchemaException
    {
        final JsonNode schemaList
            = JsonLoader.fromResource("/ref/jsonresolver-schemas.json");
        testData = JsonLoader.fromResource("/ref/jsonresolver-testdata.json");

        manager = mock(URIManager.class);
        registry = new SchemaRegistry(manager);

        final Map<String, JsonNode> map
            = CollectionUtils.toMap(schemaList.fields());

        URI uri;
        JsonNode schema;

        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            uri = URI.create(entry.getKey());
            schema = entry.getValue();
            when(manager.getContent(uri)).thenReturn(schema);
        }

        final JsonResolverBuilder builder = new JsonResolverBuilder()
            .withManager(manager).withRegistry(registry);
        resolver = builder.build();
    }

    @DataProvider
    private Iterator<Object[]> loopTestData()
        throws JsonSchemaException
    {
        final Set<Object[]> set = new HashSet<Object[]>();

        SchemaContainer container;
        SchemaNode node;

        for (final JsonNode schema: testData.get("loops")) {
            container = new SchemaContainer(schema);
            node = new SchemaNode(container, schema);
            set.add(new Object[]{ node });
        }

        return set.iterator();
    }

    @Test(
        dataProvider = "loopTestData"
    )
    public void loopsAreDetected(final SchemaNode node)
    {
        try {
            resolver.resolve(node);
            fail("No exception thrown!");
        } catch (JsonSchemaException e) {
            assertEquals(e.getMessage(), "ref loop detected");
        }
    }

    @DataProvider
    private Iterator<Object[]> resolveData()
        throws JsonSchemaException
    {
        final Set<Object[]> set = new HashSet<Object[]>();

        SchemaContainer container;
        SchemaNode node;
        JsonNode schema, expected;

        for (final JsonNode testNode: testData.get("resolve")) {
            schema = testNode.get("schema");
            container = new SchemaContainer(schema);
            node = new SchemaNode(container, schema);
            expected = testNode.get("expected");
            set.add(new Object[] { node, expected });
        }

        return set.iterator();
    }

    @Test(dataProvider = "resolveData")
    public void resolveWorksAsExpected(final SchemaNode node,
        final JsonNode expected)
        throws JsonSchemaException
    {
        final SchemaNode ret = resolver.resolve(node);
        assertEquals(ret.getNode(), expected);
    }

    @Test
    public void sameLocatorIsOnlyLookedUpOnce()
        throws JsonSchemaException
    {
        final String locator = "schema://schema4#";
        final URI uri = URI.create(locator);
        final JsonNode schema = JsonNodeFactory.instance.objectNode()
            .put("$ref", locator);
        final SchemaContainer container = new SchemaContainer(schema);
        final SchemaNode node = new SchemaNode(container, schema);

        resolver.resolve(node);
        verify(manager, times(1)).getContent(uri);
    }
}
