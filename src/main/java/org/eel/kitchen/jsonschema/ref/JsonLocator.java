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
import java.net.URISyntaxException;

public abstract class JsonLocator
{
    protected static final URI EMPTY_URI = URI.create("");

    protected final URI uri;

    protected JsonLocator(final URI uri)
    {
        this.uri = uri;
    }

    public static JsonLocator fromURI(final URI uri)
    {
        final URI normalized = uri.normalize();

        final URI realURI;

        try {
            realURI = new URI(normalized.getScheme(),
                normalized.getSchemeSpecificPart(), null);
        } catch (URISyntaxException e) {
            throw new RuntimeException("WTF??", e);
        }

        if (EMPTY_URI.equals(realURI))
            return EmptyJsonLocator.getInstance();

        return "jar".equals(realURI.getScheme())
            ? new JarJsonLocator(realURI)
            : new BaseJsonLocator(realURI);
    }

    public final boolean isAbsolute()
    {
        return uri.isAbsolute();
    }

    public final URI toURI()
    {
        return uri;
    }

    public abstract JsonLocator resolve(final JsonLocator other);

    @Override
    public final boolean equals(final Object o)
    {
        if (o == null)
            return false;
        if (this == o)
            return true;
        if (!(o instanceof JsonLocator))
            return false;

        final JsonLocator that = (JsonLocator) o;

        return uri.equals(that.uri);
    }

    @Override
    public final int hashCode()
    {
        return uri.hashCode();
    }

    @Override
    public final String toString()
    {
        return uri.toString();
    }
}
