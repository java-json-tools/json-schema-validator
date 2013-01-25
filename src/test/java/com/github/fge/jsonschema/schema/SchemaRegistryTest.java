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

package com.github.fge.jsonschema.schema;

import com.github.fge.jsonschema.main.JsonSchemaException;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.report.Domain;
import com.github.fge.jsonschema.report.Message;
import com.github.fge.jsonschema.uri.URIDownloader;
import com.github.fge.jsonschema.uri.URIManager;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
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
                return new ByteArrayInputStream(JacksonUtils.nodeFactory()
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
        final SchemaBundle bundle = new SchemaBundle();
        final String location = "http://toto/a/../b";

        bundle.addSchema(location, JacksonUtils.nodeFactory().objectNode());

        final SchemaRegistry registry = new SchemaRegistry(new URIManager(),
            URI.create("#"), AddressingMode.CANONICAL);

        registry.addBundle(bundle);

        final SchemaContext container = registry.get(URI.create(location));

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

    @Test
    public void injectedSchemasAreNotFetchedAgain()
        throws JsonSchemaException, IOException
    {
        final URI uri = URI.create("http://foo.bar/baz#");
        final SchemaBundle bundle = new SchemaBundle();

        bundle.addSchema(uri, JacksonUtils.nodeFactory().objectNode());

        final URIDownloader mock = mock(URIDownloader.class);
        final URIManager manager = new URIManager();
        manager.registerScheme("http", mock);

        final SchemaRegistry registry = new SchemaRegistry(manager,
            URI.create("#"), AddressingMode.CANONICAL);
        registry.addBundle(bundle);

        registry.get(uri);
        verify(mock, never()).fetch(uri);
    }
}
