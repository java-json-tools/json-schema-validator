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

    public SchemaContainer register(final JsonNode node)
        throws JsonSchemaException
    {
        if (node == null)
            throw new IllegalArgumentException("schema is null");

        final SchemaContainer container = new SchemaContainer(node);
        final URI locator = container.getLocator().getRootAsURI();

        if (containers.containsKey(locator))
            throw new JsonSchemaException("URI \"" + locator + "\" is "
                + "already registered");
        containers.put(locator, container);
        return container;
    }

    public synchronized SchemaContainer get(final URI uri)
        throws JsonSchemaException
    {
        SchemaContainer container = containers.get(uri);

        if (container == null) {
            container = new SchemaContainer(manager.getContent(uri));
            containers.put(uri, container);
        }

        return container;
    }

    public SchemaContainer register(final URI uri, final JsonNode schema)
        throws JsonSchemaException
    {
        final SchemaContainer container = new SchemaContainer(schema);
        containers.put(uri, container);
        return container;
    }
}
