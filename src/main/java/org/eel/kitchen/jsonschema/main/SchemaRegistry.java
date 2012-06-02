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

import org.eel.kitchen.jsonschema.ref.JsonRef;
import org.eel.kitchen.jsonschema.schema.SchemaContainer;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public final class SchemaRegistry
{
    private final Map<URI, SchemaContainer> containers
        = new HashMap<URI, SchemaContainer>();

    /**
     * Register a container with a given URI
     *
     * <p>You will normally never call this yourself. There is only one
     * anomalous condition which throws a checked exception.</p>
     *
     * @param uri the URI of the container
     * @param container the container
     * @throws JsonSchemaException container's URI and registering URI are
     * not the same
     * @throws IllegalArgumentException attempt to register a null URI or
     * container, or to register the same URI twice
     */
    public void register(final URI uri, final SchemaContainer container)
        throws JsonSchemaException
    {
        if (uri == null)
            throw new IllegalArgumentException("uri is null");
        if (container == null)
            throw new IllegalArgumentException("container is null");

        if (containers.containsKey(uri))
            throw new IllegalArgumentException("URI \"" + uri + "\" already "
                + "registered");

        // FIXME: namespace
        final JsonRef ref = container.getLocator();
        if (ref.isAbsolute()) {
            final URI locator = ref.getLocator();
            if (!uri.equals(locator))
                throw new JsonSchemaException("URI mismatch: schema has "
                    + "locator \"" + locator + "\", but tried to register as \""
                    + uri + '"');
        }

        containers.put(uri, container);
    }

    public SchemaContainer get(final URI uri)
    {
        return containers.get(uri);
    }
}
