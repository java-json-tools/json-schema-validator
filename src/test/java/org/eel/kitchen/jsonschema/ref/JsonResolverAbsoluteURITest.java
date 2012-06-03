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
    private JsonNode schemaList;
    private JsonNode testData;
    private URIManager manager;
    private JsonResolver resolver;

    @BeforeClass
    public void setUp()
        throws IOException, JsonSchemaException
    {
        schemaList = JsonLoader.fromResource("/ref/jsonresolver-absolute.json");
        testData = JsonLoader.fromResource("/ref/jsonresolver-testdata.json");

        manager = mock(URIManager.class);

        final Map<String, JsonNode> map
            = CollectionUtils.toMap(schemaList.fields());

        URI uri;
        JsonNode schema;

        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            uri = URI.create(entry.getKey());
            schema = entry.getValue();
            when(manager.getContent(uri)).thenReturn(schema);
        }

        resolver = new JsonResolver(manager);
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

}
