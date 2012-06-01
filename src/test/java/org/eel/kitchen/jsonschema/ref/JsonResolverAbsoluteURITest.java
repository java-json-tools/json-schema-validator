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
import org.eel.kitchen.jsonschema.schema.SchemaNode;
import org.eel.kitchen.jsonschema.uri.URIManager;
import org.eel.kitchen.util.CollectionUtils;
import org.eel.kitchen.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class JsonResolverAbsoluteURITest
{
    private static final String SELF_REFERENCING = "a://b.c#";

    private JsonResolver resolver;
    private Map<String, JsonNode> schemas = new HashMap<String, JsonNode>();

    @BeforeClass
    public void initializeResolver()
        throws IOException, JsonSchemaException
    {
        final JsonNode testData
            = JsonLoader.fromResource("/ref/jsonresolver-absolute.json");

        schemas = CollectionUtils.toMap(testData.fields());

        final URIManager manager = mock(URIManager.class);

        URI uri;
        JsonNode node;

        for (final Map.Entry<String, JsonNode> entry: schemas.entrySet()) {
            uri = URI.create(entry.getKey());
            node = entry.getValue();
            when(manager.getContent(uri)).thenReturn(node);
        }

        resolver = new JsonResolver(manager);
    }

    @Test(timeOut = 5000)
    public void referencingAbsoluteSelfIsDetectedAsLoop()
        throws JsonSchemaException
    {
        final JsonNode schema = schemas.get(SELF_REFERENCING);

        final SchemaContainer container = new SchemaContainer(schema);
        final SchemaNode node = new SchemaNode(container, schema);

        try {
            resolver.resolve(node);
            fail("No exception thrown!");
        } catch (JsonSchemaException e) {
            assertEquals(e.getMessage(), "ref loop detected");
        }
    }
}
