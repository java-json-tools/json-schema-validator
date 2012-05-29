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

package org.eel.kitchen.jsonschema.ref;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

class SchemaRegistry
{
    private final Map<URI, SchemaContainer> containers
        = new HashMap<URI, SchemaContainer>();

    public void register(final URI uri, final SchemaContainer container)
    {
        if (uri == null)
            throw new IllegalArgumentException("uri is null");
        if (container == null)
            throw new IllegalArgumentException("container is null");
        containers.put(uri, container);
    }

    public SchemaContainer get(final URI uri)
    {
        return containers.get(uri);
    }
}
