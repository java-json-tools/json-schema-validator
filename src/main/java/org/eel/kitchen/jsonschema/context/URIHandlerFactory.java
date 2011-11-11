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

package org.eel.kitchen.jsonschema.context;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.util.HTTPURIHandler;
import org.eel.kitchen.util.URIHandler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public final class URIHandlerFactory
    implements URIHandler
{
    private final Map<String, URIHandler> schemeHandlers
        = new HashMap<String, URIHandler>();

    private JsonNode localSchema;

    URIHandlerFactory()
    {
        schemeHandlers.put("http", new HTTPURIHandler());
    }

    //FIXME: risky
    public void setLocalSchema(final JsonNode schema)
    {
        localSchema = schema;
    }

    public URIHandler getHandler(final String ref)
    {
        final URI uri;

        try {
            uri = noFragmentsURI(ref);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("PROBLEM: invalid URI found ("
                + ref + "), syntax validation should have caught that", e);
        }

        if (!uri.isAbsolute()) {
            if (!uri.getSchemeSpecificPart().isEmpty())
                throw new IllegalArgumentException("invalid URI " + ref
                    + ": URI is not absolute but it does not have a scheme "
                    + "either");
            return this;
        }

        final String scheme = uri.getScheme();

        final URIHandler ret = schemeHandlers.get(scheme);

        if (ret == null)
            throw new IllegalArgumentException("unsupported scheme " + scheme);

        return ret;
    }

    private static URI noFragmentsURI(final String ref)
        throws URISyntaxException
    {
        final URI uri = new URI(ref);
        return new URI(uri.getScheme(), uri.getSchemeSpecificPart(), null);
    }

    @Override
    public JsonNode getDocument(final URI uri)
        throws IOException
    {
        return localSchema;
    }
}
