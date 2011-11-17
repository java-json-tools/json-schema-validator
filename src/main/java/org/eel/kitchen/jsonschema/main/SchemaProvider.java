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

package org.eel.kitchen.jsonschema.main;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.uri.URIHandler;
import org.eel.kitchen.jsonschema.uri.URIHandlerFactory;
import org.eel.kitchen.util.JsonPointer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public final class SchemaProvider
{
    private static final URI ANONYMOUS_ID;

    static {
        try {
            ANONYMOUS_ID = new URI("");
        } catch (URISyntaxException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private URIHandlerFactory factory;

    private Map<URI, JsonNode> locators;

    private URI currentLocation;

    private JsonNode schema;

    public SchemaProvider(final JsonNode schema)
    {
        this.schema = schema;

        if (schema == null)
            return;

        factory = new URIHandlerFactory();
        locators = new HashMap<URI, JsonNode>();

        final URI root;

        try {
            root = schema.path("id").isTextual()
                ? new URI(schema.get("id").getTextValue())
                : ANONYMOUS_ID;
            locators.put(root, schema);
            currentLocation = root;
        } catch (URISyntaxException ignored) {
            // should not happen
        }
    }

    private SchemaProvider()
    {
    }

    public SchemaProvider atPoint(final JsonPointer pointer)
    {
        final SchemaProvider ret = new SchemaProvider();
        ret.currentLocation = currentLocation;
        ret.factory = factory;
        ret.locators = locators;
        ret.schema = pointer.getPath(locators.get(currentLocation));
        return ret;
    }

    public SchemaProvider withSchema(final JsonNode newschema)
    {
        final SchemaProvider ret = new SchemaProvider();
        ret.factory = factory;
        ret.locators = locators;
        ret.schema = newschema;
        ret.currentLocation = currentLocation;
        return ret;
    }

    public SchemaProvider atURI(final URI uri)
        throws IOException
    {
        if (!uri.isAbsolute()) {
            if (!uri.getSchemeSpecificPart().isEmpty())
                throw new IllegalArgumentException("invalid URI: "
                    + "URI is not absolute and is not a JSON Pointer either");
            return this;
        }

        final SchemaProvider ret = new SchemaProvider();
        ret.factory = factory;
        ret.currentLocation = uri;
        ret.locators = locators;
        if (locators.containsKey(uri)) {
            ret.schema = locators.get(uri);
            return ret;
        }

        ret.schema = factory.getHandler(uri).getDocument(uri);
        locators.put(uri, ret.schema);
        return ret;
    }

    public JsonNode getSchema()
    {
        return schema;
    }

    public void registerHandler(final String scheme,
        final URIHandler handler)
    {
        factory.registerHandler(scheme, handler);
    }

    public void unregisterHandler(final String scheme)
    {
        factory.unregisterHandler(scheme);
    }
}
