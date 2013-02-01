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

package com.github.fge.jsonschema.processing.ref;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.main.JsonSchemaException;
import com.github.fge.jsonschema.processing.LogLevel;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.uri.URIDownloader;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static com.github.fge.jsonschema.messages.RefProcessingMessages.*;
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
        throws IOException, ProcessingException
    {
        final InputStream sampleStream
            = new ByteArrayInputStream("{}".getBytes());

        when(mock.fetch(any(URI.class))).thenReturn(sampleStream);

        manager.registerScheme("foo", mock);

        final URI uri = URI.create("foo://bar");

        manager.getContent(uri);
        verify(mock, times(1)).fetch(uri);
    }

    @Test(dependsOnMethods = "shouldBeAbleToRegisterScheme")
    public void shouldBeAbleToUnregisterScheme()
        throws IOException, ProcessingException
    {
        final URI uri = URI.create("foo://bar");
        final InputStream sampleStream
            = new ByteArrayInputStream("{}".getBytes());

        when(mock.fetch(any(URI.class))).thenReturn(sampleStream);

        manager.registerScheme("foo", mock);
        manager.getContent(uri);

        manager.unregisterScheme("foo");

        try {
            manager.getContent(uri);
            fail("No exception thrown!");
        } catch (ProcessingException e) {
            assertMessage(e.getProcessingMessage()).hasMessage(UNHANDLED_SCHEME)
                .hasLevel(LogLevel.FATAL).hasField("scheme", "foo")
                .hasField("uri", uri);
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
        throws ProcessingException
    {
        try {
            manager.getContent(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "null URI");
        }
    }

    @Test
    public void unhandledSchemeShouldBeReportedAsSuch()
    {
        manager.registerScheme("foo", mock);
        final URI uri = URI.create("bar://baz");

        try {
            manager.getContent(uri);
        } catch (ProcessingException e) {
            assertMessage(e.getProcessingMessage()).hasMessage(UNHANDLED_SCHEME)
                .hasField("scheme", "bar").hasField("uri", uri)
                .hasLevel(LogLevel.FATAL);
        }
    }

    @Test
    public void downloaderProblemsShouldBeReportedAsSuch()
        throws IOException
    {
        final URI uri = URI.create("foo://bar");
        final Exception foo = new IOException("foo");

        when(mock.fetch(any(URI.class))).thenThrow(foo);

        manager.registerScheme("foo", mock);

        final ProcessingMessage msg = new ProcessingMessage()
            .setLogLevel(LogLevel.FATAL).put("uri", uri)
            .msg("cannot dereference URI (IOException)")
            .put("exceptionMessage", "foo");

        try {
            manager.getContent(uri);
        } catch (ProcessingException e) {
            assertEquals(e.getProcessingMessage(), msg);
        }
    }

    @Test
    public void nonJSONInputShouldBeReportedAsSuch()
        throws IOException
    {
        final URI uri = URI.create("foo://bar");
        final InputStream sampleStream
            = new ByteArrayInputStream("}".getBytes());

        when(mock.fetch(any(URI.class))).thenReturn(sampleStream);

        manager.registerScheme("foo", mock);

        final ProcessingMessage msg = new ProcessingMessage().put("uri", uri)
            .setLogLevel(LogLevel.FATAL)
            .msg("content at URI is not valid JSON");
        final JsonNode expected = msg.asJson();

        try {
            manager.getContent(uri);
        } catch (ProcessingException e) {
            final ObjectNode actual
                = (ObjectNode) e.getProcessingMessage().asJson();
            assertTrue(actual.path("parsingMessage").isTextual());
            actual.remove("parsingMessage");
            assertEquals(actual, expected);
        }
    }

    @Test
    public void URIRedirectionIsFollowed()
        throws JsonSchemaException, IOException, ProcessingException
    {
        /*
         * The content we return
         */
        final JsonNode expected = JacksonUtils.nodeFactory().objectNode()
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

        final URI source = JsonRef.fromString(from).getLocator();
        final URI target = JsonRef.fromString(to).getLocator();

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
