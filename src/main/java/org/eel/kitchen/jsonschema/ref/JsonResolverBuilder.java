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

import org.eel.kitchen.jsonschema.main.SchemaRegistry;
import org.eel.kitchen.jsonschema.uri.URIManager;

public final class JsonResolverBuilder
{
    private SchemaRegistry registry;
    private URIManager manager;

    public JsonResolverBuilder withRegistry(final SchemaRegistry registry)
    {
        this.registry = registry;
        return this;
    }

    public JsonResolverBuilder withManager(final URIManager manager)
    {
        this.manager = manager;
        return this;
    }

    public JsonResolver build()
    {
        return new JsonResolver(manager, registry);
    }
}
