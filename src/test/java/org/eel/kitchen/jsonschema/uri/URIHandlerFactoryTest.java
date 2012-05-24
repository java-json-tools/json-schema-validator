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

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class URIHandlerFactoryTest
{
    private URIHandlerFactory factory;

    @BeforeMethod
    public void setUp()
    {
        factory = new URIHandlerFactory();
    }

    @Test
    public void nonAbsoluteURIsShouldBeRejected()
        throws IOException
    {
        final URI uri = URI.create("foo");

        try {
            factory.getDocument(uri);
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "only absolute URIs are supported");
        }
    }

    @Test
    public void nonRegisteredSchemeRaisesAnException()
        throws IOException
    {
        final URI uri = URI.create("ftp://x.y/t.json");
        factory.unregisterHandler("ftp");

        try {
            factory.getDocument(uri);
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "unsupported scheme ftp");
        }
    }

    @Test
    public void cannotRegisterTwiceForTheSameScheme()
    {
        final URIHandler mock = mock(URIHandler.class);

        factory.registerHandler("ftp", mock);

        try {
            factory.registerHandler("ftp", mock);
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "scheme ftp already registered");
        }
    }

    @Test
    public void cannotRegisterIllegalScheme()
    {
        final URIHandler mock = mock(URIHandler.class);

        try {
            factory.registerHandler("+23", mock);
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "invalid scheme +23");
        }
    }

    @Test
    public void cannotRegisterNullHandler()
    {
        try {
            factory.registerHandler("ftp", null);
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "handler is null");
        }
    }

    @Test
    public void correctHandlerGetsCalledOnDownload()
        throws IOException
    {
        final URI uri = URI.create("ftp://foo.bar/t.json");
        final URIHandler mock = mock(URIHandler.class);

        when(mock.getDocument(any(URI.class)))
            .thenReturn(JsonNodeFactory.instance.objectNode());

        factory.registerHandler("ftp", mock);
        factory.getDocument(uri);

        verify(mock).getDocument(uri);
    }
}
