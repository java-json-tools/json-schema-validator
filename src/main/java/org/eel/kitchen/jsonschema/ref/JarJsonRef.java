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

public final class JarJsonRef
    extends JsonRef
{
    private final String jarPrefix;
    private final URI pathURI;

    JarJsonRef(final URI uri)
    {
        super(uri);
        final String str = uri.toString();
        final int index = str.indexOf('!');

        Preconditions.checkArgument(index != -1, "malformed JAR URL " + str);
        jarPrefix = str.substring(0, index + 1);

        final String path = str.substring(index + 1);
        Preconditions.checkArgument(path.startsWith("/"), "malformed JAR URL "
            + str);
        pathURI = URI.create(path);
    }

    private JarJsonRef(final URI uri, final String jarPrefix, final URI pathURI)
    {
        super(uri);
        this.jarPrefix = jarPrefix;
        this.pathURI = pathURI;
    }

    @Override
    public boolean isAbsolute()
    {
        return fragment.isEmpty();
    }

    @Override
    public JsonRef resolve(final JsonRef other)
    {
        if (other.uri.isAbsolute())
            return other;

        final URI targetPath = pathURI.resolve(other.uri);
        final URI targetURI = URI.create(jarPrefix + targetPath.toString());
        return new JarJsonRef(targetURI, jarPrefix, targetPath);
    }
}
