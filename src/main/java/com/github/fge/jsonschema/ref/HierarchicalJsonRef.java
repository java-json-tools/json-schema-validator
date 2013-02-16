/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.jsonschema.ref;

import java.net.URI;

/**
 * JSON Reference for classical, hierarchical URIs
 *
 * <p>A hierarchical URI is defined as a URI which is either not absolute, or
 * which is absolute but not opaque. Resolution of such URIs can therefore
 * proceed as described in <a href="http://tools.ietf.org/html/rfc3986">RFC 3986
 * </a>.</p>
 *
 * <p>An example of URIs which are both absolute and opaque are jar URLs, which
 * have a dedicated class for this reason ({@link JarJsonRef}).</p>
 */
final class HierarchicalJsonRef
    extends JsonRef
{
    HierarchicalJsonRef(final URI uri)
    {
        super(uri);
    }

    @Override
    public boolean isAbsolute()
    {
        if (!legal)
            return false;
        return locator.isAbsolute() && pointer.isEmpty();
    }

    @Override
    public JsonRef resolve(final JsonRef other)
    {
        return fromURI(uri.resolve(other.uri));
    }
}
