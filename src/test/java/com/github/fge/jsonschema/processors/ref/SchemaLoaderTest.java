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

package com.github.fge.jsonschema.processors.ref;

import com.github.fge.jsonschema.cfg.LoadingConfiguration;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.load.SchemaLoader;
import com.github.fge.jsonschema.load.URIDownloader;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.report.LogLevel;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.JacksonUtils;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static com.github.fge.jsonschema.messages.RefProcessingMessages.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class SchemaLoaderTest
{
    private static final byte[] BYTES = JacksonUtils.nodeFactory().objectNode()
        .toString().getBytes();
    @Test
    public void namespacesAreRespected()
        throws ProcessingException, IOException
    {
        final URI fullPath = URI.create("foo:/baz#");
        final URIDownloader downloader = spy(new URIDownloader()
        {
            @Override
            public InputStream fetch(final URI source)
                throws IOException
            {
                if (!fullPath.equals(source))
                    throw new IOException();
                return new ByteArrayInputStream(BYTES);
            }
        });

        final String namespace = "foo:///bar/../bar/";
        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
            .addScheme("foo", downloader).setNamespace(namespace).freeze();

        final URI rootns = URI.create(namespace);

        final SchemaLoader loader = new SchemaLoader(cfg);

        final URI uri = URI.create("../baz");
        loader.get(uri);
        final JsonRef ref = JsonRef.fromURI(rootns.resolve(uri));
        verify(downloader).fetch(rootns.resolve(ref.toURI()));
    }

    @Test
    public void URIsAreNormalizedBehindTheScenes()
        throws ProcessingException
    {
        final String location = "http://toto/a/../b";
        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
            .preloadSchema(location, JacksonUtils.nodeFactory().objectNode())
            .freeze();

        final SchemaLoader loader = new SchemaLoader(cfg);

        final SchemaTree tree = loader.get(URI.create(location));

        assertEquals(tree.getLoadingRef().toURI(),
            URI.create("http://toto/b#"));
    }

    @Test
    public void NonAbsoluteURIsAreRefused()
    {
        final SchemaLoader loader = new SchemaLoader();

        final URI target = URI.create("moo#");

        try {
            loader.get(target);
            fail("No exception thrown!");
        } catch (ProcessingException e) {
            assertMessage(e.getProcessingMessage()).hasMessage(URI_NOT_ABSOLUTE)
                .hasLevel(LogLevel.FATAL).hasField("uri", target);
        }
    }

    @Test
    public void injectedSchemasAreNotFetchedAgain()
        throws ProcessingException, IOException
    {
        final String location = "http://foo.bar/baz#";
        final URI uri = URI.create(location);
        final URIDownloader mock = mock(URIDownloader.class);
        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
            .addScheme("http", mock)
            .preloadSchema(location, JacksonUtils.nodeFactory().objectNode())
            .freeze();
        final SchemaLoader registry = new SchemaLoader(cfg);

        registry.get(uri);
        verify(mock, never()).fetch(uri);
    }

    @Test
    public void schemasAreFetchedOnceNotTwice()
        throws ProcessingException, IOException
    {
        final URI uri = URI.create("foo:/baz#");
        final URIDownloader downloader = spy(new URIDownloader()
        {
            @Override
            public InputStream fetch(final URI source)
                throws IOException
            {
                return new ByteArrayInputStream(BYTES);
            }
        });

        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
            .addScheme("foo", downloader).freeze();
        final SchemaLoader loader = new SchemaLoader(cfg);

        loader.get(uri);
        loader.get(uri);
        verify(downloader, times(1)).fetch(uri);
    }
}
