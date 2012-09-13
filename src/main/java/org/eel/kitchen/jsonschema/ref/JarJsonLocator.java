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

import com.google.common.base.Preconditions;

import java.net.URI;

final class JarJsonLocator
    extends JsonLocator
{
    private final String jarPrefix;
    private final URI path;

    JarJsonLocator(final URI uri)
    {
        super(uri);
        final String str = this.uri.toString();

        final int index = str.indexOf('!');
        Preconditions.checkArgument(index != -1, "illegal jar URL " + this.uri);

        jarPrefix = str.substring(0, index + 1);

        final String pathstr = str.substring(index + 1);
        Preconditions.checkArgument(pathstr.startsWith("/"), "illegal jar URL "
            + this.uri);

        path = URI.create(pathstr);
    }

    private JarJsonLocator(final String jarPrefix, final URI path)
    {
        super(URI.create(jarPrefix + path.toString()));
        this.jarPrefix = jarPrefix;
        this.path = path;
    }

    @Override
    public JsonLocator resolve(final JsonLocator other)
    {
        if (other.isAbsolute())
            return other;

        final String targetPath = path.resolve(other.uri).normalize()
            .toString();

        return new JarJsonLocator(jarPrefix, URI.create(targetPath));
    }
}
