/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.uri;

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

        schemeHandlers.put(scheme, handler);
    }

    /**
     * Unregister a handler for a given scheme
     *
     * @param scheme the victim
     */
    public void unregisterHandler(final String scheme)
    {
        schemeHandlers.remove(scheme);
    }

    /**
     * Get a handler for the given URI
     *
     * @param uri the URI
     * @return the handler
     * @throws IllegalArgumentException the URI is not absolute,
     * or the scheme of this URI is not registered
     */
    public URIHandler getHandler(final URI uri)
    {
        final String scheme = uri.getScheme();

        if (scheme == null)
            throw new IllegalArgumentException("only absolute URIs are "
                + "supported");

        final URIHandler ret = schemeHandlers.get(scheme);

        if (ret == null)
            throw new IllegalArgumentException("unsupported scheme " + scheme);

        return ret;
    }
}
