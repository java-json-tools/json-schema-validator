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
import org.eel.kitchen.jsonschema.uri.URIDownloader;
import org.eel.kitchen.jsonschema.uri.URIManager;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class SchemaRegistryTest
{
    @Test
    public void namespacesAreRespected()
        throws IOException, JsonSchemaException
    {
        final URI fullPath = URI.create("foo:///baz");
        final URIManager manager = new URIManager();
        final URIDownloader downloader = spy(new URIDownloader()
        {
            @Override
            public InputStream fetch(final URI source)
                throws IOException
            {
                if (!fullPath.equals(source))
                    throw new IOException();
                return new ByteArrayInputStream(JsonNodeFactory.instance
                    .objectNode().toString().getBytes());
            }
        });
        manager.registerScheme("foo", downloader);

        final URI rootns = URI.create("foo:///bar/../bar/");

        final SchemaRegistry registry = new SchemaRegistry(manager, rootns);

        final URI uri = URI.create("../baz");
        registry.get(uri);
        verify(downloader).fetch(rootns.resolve(uri));
    }

    @Test
    public void URIsAreNormalizedBehindTheScenes()
        throws JsonSchemaException
    {
        final JsonNode schema = JsonNodeFactory.instance.objectNode()
            .put("id", "http://toto/a/../b");

        final SchemaRegistry registry = new SchemaRegistry(new URIManager(),
            URI.create("#"));

        final SchemaContainer container = registry.register(schema);

        assertEquals(container.getLocator().getRootAsURI(),
            URI.create("http://toto/b#"));
    }
}
