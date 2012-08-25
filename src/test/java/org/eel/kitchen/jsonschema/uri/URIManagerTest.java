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

package org.eel.kitchen.jsonschema.uri;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.ref.JsonRef;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class URIManagerTest
{
    private URIManager manager;
    private URIDownloader mock;

    @BeforeMethod
    public void setUp()
    {
        manager = new URIManager();
        mock = mock(URIDownloader.class);
    }

    @Test
    public void shouldBeAbleToRegisterScheme()
        throws JsonSchemaException, IOException
    {
        final InputStream sampleStream
            = new ByteArrayInputStream("{}".getBytes());

        when(mock.fetch(any(URI.class))).thenReturn(sampleStream);

        manager.registerScheme("foo", mock);

        manager.getContent(URI.create("foo://bar"));

        assertTrue(true);
    }

    @Test(dependsOnMethods = "shouldBeAbleToRegisterScheme")
    public void shouldBeAbleToUnregisterScheme()
        throws IOException, JsonSchemaException
    {
        final InputStream sampleStream
            = new ByteArrayInputStream("{}".getBytes());

        when(mock.fetch(any(URI.class))).thenReturn(sampleStream);

        manager.registerScheme("foo", mock);
        manager.getContent(URI.create("foo://bar"));

        manager.unregisterScheme("foo");
        try {
            manager.getContent(URI.create("foo://bar"));
            fail("No exception thrown!");
        } catch (JsonSchemaException e) {
            assertEquals(e.getMessage(), "cannot handle scheme \"foo\""
                + " (requested URI: foo://bar)");
        }
    }
    @Test
    public void shouldNotBeAbleToRegisterNullScheme()
    {
        try {
            manager.registerScheme(null, mock);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "scheme is null");
        }
    }

    @Test
    public void shouldNotBeAbleToRegisterEmptyScheme()
    {
        try {
            manager.registerScheme("", mock);
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "scheme is empty");
        }
    }

    @Test
    public void shouldNotBeAbleToRegisterAnIllegalScheme()
    {
        try {
            manager.registerScheme("+23", mock);
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "illegal scheme \"+23\"");
        }
    }

    @Test
    public void shouldHandleNullURI()
        throws JsonSchemaException
    {
        try {
            manager.getContent(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "null URI");
        }
    }

    @Test
    public void shouldRefuseToHandleNonAbsoluteURIs()
        throws JsonSchemaException
    {
        try {
            manager.getContent(URI.create(""));
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "requested URI () is not absolute");
        }
    }

    @Test
    public void unhandledSchemeShouldBeReportedAsSuch()
    {
        manager.registerScheme("foo", mock);

        try {
            manager.getContent(URI.create("bar://baz"));
        } catch (JsonSchemaException e) {
            assertEquals(e.getMessage(), "cannot handle scheme \"bar\" "
                + "(requested URI: bar://baz)");
        }
    }

    @Test
    public void downloaderProblemsShouldBeReportedAsSuch()
        throws IOException
    {
        final Exception foo = new IOException("foo");
        when(mock.fetch(any(URI.class))).thenThrow(foo);

        manager.registerScheme("foo", mock);

        try {
            manager.getContent(URI.create("foo://bar"));
        } catch (JsonSchemaException e) {
            assertEquals(e.getMessage(), "cannot fetch content from URI "
                + "\"foo://bar\"");
            assertEquals(e.getCause(), foo);
        }
    }

    @Test
    public void nonJSONInputShouldBeReportedAsSuch()
        throws IOException
    {
        final InputStream sampleStream
            = new ByteArrayInputStream("}".getBytes());

        when(mock.fetch(any(URI.class))).thenReturn(sampleStream);

        manager.registerScheme("foo", mock);

        try {
            manager.getContent(URI.create("foo://bar"));
        } catch (JsonSchemaException e) {
            assertEquals(e.getMessage(), "content fetched from URI "
                +"\"foo://bar\" is not valid JSON");
        }
    }

    @Test
    public void URIRedirectionIsFollowed()
        throws IOException, JsonSchemaException
    {
        /*
         * The content we return
         */
        final JsonNode expected = JsonNodeFactory.instance.objectNode()
            .put("hello", "world");
        final InputStream sampleStream
            = new ByteArrayInputStream(expected.toString().getBytes());

        /*
         * We need to build both the source URI and destination URI. As they are
         * both transformed to valid JSON References internally, we also build
         * JsonRef-compatible URIs (ie, with a fragment, even empty).
         *
         * The user, however, may supply URIs which are not JsonRef-compatible.
         */
        final String from = "http://some.site/schema.json";
        final String to = "foo://real/location.json";
        manager.addRedirection(from, to);

        final URI source = JsonRef.fromString(from).getRootAsURI();
        final URI target = JsonRef.fromString(to).getRootAsURI();

        /*
         * Build another mock for the original source URI protocol, make it
         * return the same thing as the target URI. Register both downloaders.
         */
        final URIDownloader httpMock = mock(URIDownloader.class);
        when(httpMock.fetch(source)).thenReturn(sampleStream);
        manager.registerScheme("http", httpMock);

        when(mock.fetch(target)).thenReturn(sampleStream);
        manager.registerScheme("foo", mock);

        /*
         * Get the original source...
         */
        final JsonNode actual = manager.getContent(source);

        /*
         * And verify that it has been downloaded from the target, not the
         * source
         */
        verify(httpMock, never()).fetch(any(URI.class));
        verify(mock).fetch(target);

        /*
         * Finally, ensure the correctness of the downloaded content.
         */
        assertEquals(actual, expected);
    }
}
