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

package org.eel.kitchen.jsonschema.main;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.ref.JsonRef;
import org.eel.kitchen.jsonschema.schema.SchemaContainer;
import org.eel.kitchen.jsonschema.uri.URIManager;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class SchemaRegistry
{
    private final Map<URI, SchemaContainer> containers
        = new HashMap<URI, SchemaContainer>();

    private final URIManager manager;

    public SchemaRegistry()
    {
        manager = new URIManager();
    }

    public SchemaRegistry(final URIManager manager)
    {
        this.manager = manager;
    }

    public SchemaContainer register(final JsonNode node)
        throws JsonSchemaException
    {
        if (node == null)
            throw new IllegalArgumentException("schema is null");

        if (!node.has("id"))
            throw new JsonSchemaException("schema has no locator");

        final SchemaContainer container = new SchemaContainer(node);
        final URI uri = container.getLocator().getRootAsURI();

        if (containers.containsKey(uri))
            throw new JsonSchemaException("URI \"" + uri + "\" is already "
                + "registered");
        containers.put(uri, container);
        return container;
    }

    public synchronized SchemaContainer get(final URI uri)
        throws JsonSchemaException
    {
        SchemaContainer container = containers.get(uri);

        if (container == null) {
            container = new SchemaContainer(manager.getContent(uri));
            final JsonRef expected = new JsonRef(uri);
            final JsonRef actual = container.getLocator();
            if (!actual.equals(expected))
                throw new JsonSchemaException("URI and id of downloaded "
                    + "schema disagree (URI: " + expected + ", id: " + actual
                    + ")");
            containers.put(uri, container);
        }

        return container;
    }
}
