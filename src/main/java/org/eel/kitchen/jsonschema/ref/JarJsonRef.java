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

/**
 * Special case of a JSON Reference with a JAR URL
 *
 * <p>These URLs are legal URIs, but they are very strange beasts. Basically:
 * </p>
 *
 * <ul>
 *     <li>they are always absolute (except with a non empty fragment);</li>
 *     <li>when resolving against another URI, we special case relative URIs
 *     so as to resolve them against the path/fragment part instead of the
 *     whole URI.</li>
 * </ul>
 *
 * <p>The latter is because JAR URLs are opaque, and normally, resolving any
 * URI against an opaque URI gives the URI to be resolved.</p>
 */
final class JarJsonRef
    extends JsonRef
{
    /**
     * The URL part with the {@code !} included
     */
    private final String jarPrefix;

    /**
     * Everything after the {@code !}
     */
    private final URI pathURI;

    /**
     * Build a JSON Reference form a JAR URL
     *
     * @param uri the URI
     */
    JarJsonRef(final URI uri)
    {
        super(uri);
        final String str = uri.toString();
        final int index = str.indexOf('!');
        jarPrefix = str.substring(0, index + 1);

        final String path = str.substring(index + 1);
        pathURI = URI.create(path);
    }

    /**
     * Specialized constructor used when resolving against a relative URI
     *
     * @param uri the final URI
     * @param jarPrefix the jar prefix
     * @param pathURI the path
     */
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
