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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory providing {@link URIHandler} instances
 *
 * <p>This looks at the scheme of the URI to determine what handler is
 * returned. You can register handlers for any scheme of you choice,
 * but if you do so, remember about interoperability!</p>
 */
public final class URIHandlerFactory
{
    private static final Logger logger
        = LoggerFactory.getLogger(URIHandlerFactory.class);

    /**
     * Map pairing schemes by name to their handlers
     */
    private final Map<String, URIHandler> schemeHandlers
        = new HashMap<String, URIHandler>();

    /**
     * Constructor. Right now the only thing it does it registering a {@link
     * HTTPURIHandler}, certainly the mostly used one -- which is why it is
     * already provided
     */
    public URIHandlerFactory()
    {
        schemeHandlers.put("http", new HTTPURIHandler());
    }

    /**
     * Register a handler for a new scheme
     *
     * @param scheme the scheme
     * @param handler the handler
     * @throws IllegalArgumentException the scheme is invalid,
     * a handler is already registered for that scheme, or the handler is null
     */
    public void registerHandler(final String scheme, final URIHandler handler)
    {
        if (scheme == null)
            throw new IllegalArgumentException("scheme is null");

        try {
            new URI(scheme, "x", null);
        } catch (URISyntaxException ignored) {
            throw new IllegalArgumentException("invalid scheme " + scheme);
        }

        if (schemeHandlers.containsKey(scheme))
            throw new IllegalArgumentException("scheme " + scheme + " already"
                + " registered");

        if (handler == null)
            throw new IllegalArgumentException("handler is null");

        logger.debug("registering URI handler for scheme {}", scheme);
        schemeHandlers.put(scheme, handler);
    }

    /**
     * Unregister a handler for a given scheme
     *
     * @param scheme the victim
     */
    public void unregisterHandler(final String scheme)
    {
        logger.debug("unregistering handler for scheme {}", scheme);
        schemeHandlers.remove(scheme);
    }

    public JsonNode getDocument(final URI uri)
        throws IOException
    {
        final String scheme = uri.getScheme();

        if (scheme == null)
            throw new IllegalArgumentException("only absolute URIs are "
                + "supported");

        final URIHandler ret = schemeHandlers.get(scheme);
        if (ret == null)
            throw new IllegalArgumentException("unsupported scheme " + scheme);

        return ret.getDocument(uri);
    }
}
