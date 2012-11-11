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

package org.eel.kitchen.jsonschema.schema;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.ref.JsonRef;
import org.eel.kitchen.jsonschema.report.Domain;
import org.eel.kitchen.jsonschema.report.Message;
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
        final URI fullPath = URI.create("foo:/baz#");
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

        final SchemaRegistry registry = new SchemaRegistry(manager, rootns,
            AddressingMode.CANONICAL);

        final URI uri = URI.create("../baz");
        registry.get(uri);
        final JsonRef ref = JsonRef.fromURI(rootns.resolve(uri));
        verify(downloader).fetch(rootns.resolve(ref.toURI()));
    }

    @Test
    public void URIsAreNormalizedBehindTheScenes()
        throws JsonSchemaException
    {
        final SchemaBundle bundle;
        final String location = "http://toto/a/../b";

        bundle = SchemaBundle.withRootSchema(location,
            JsonNodeFactory.instance.objectNode());

        final SchemaRegistry registry = new SchemaRegistry(new URIManager(),
            URI.create("#"), AddressingMode.CANONICAL);

        registry.addBundle(bundle);

        final SchemaContainer container
            = registry.get(URI.create(location));

        assertEquals(container.getLocator().getLocator(),
            URI.create("http://toto/b#"));
    }

    @Test
    public void NonAbsoluteURIsAreRefused()
    {
        final SchemaRegistry registry = new SchemaRegistry(new URIManager(),
            URI.create("#"), AddressingMode.CANONICAL);

        final URI target = URI.create("moo#");

        final Message expectedMessage = Domain.REF_RESOLVING.newMessage()
            .setFatal(true).setKeyword("N/A").addInfo("uri", target)
            .setMessage("URI is not absolute").build();

        try {
            registry.get(target);
            fail("No exception thrown!");
        } catch (JsonSchemaException e) {
            assertEquals(e.getValidationMessage(), expectedMessage);
        }
    }
}
