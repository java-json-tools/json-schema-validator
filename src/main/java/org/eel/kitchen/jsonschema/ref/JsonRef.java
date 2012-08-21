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
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Representation of a JSON Reference
 *
 * <p><a href="http://tools.ietf.org/html/draft-pbryan-zyp-json-ref-02">
 *     JSON Reference</a>,
 * currently a draft, is a way to address a JSON instance of whatever type.</p>
 *
 * <p>To quote the draft, "A JSON Reference is a JSON object, which contains
 * a member named "$ref", which has a JSON string value." This string value
 * must be a URI. Example:</p>
 *
 * <pre>
 *     {
 *         "$ref": "http://example.com/example.json#/foo/bar"
 *     }
 * </pre>
 *
 * <p>Here we choose to derive a little from the specification and calculate
 * references from any field, not just {@code $ref}. This class is also used,
 * for instance, to compute {@code id}.
 * </p>
 *
 * <p>The implementation is a wrapper over Java's {@link URI},
 * with the following differences:</p>
 *
 * <ul>
 *     <li>all URIs are normalized from the get go;</li>
 *     <li>an empty fragment is equivalent to no fragment at all,
 *     and stands for a root JSON Pointer;</li>
 *     <li>a reference is taken to be absolute if the underlying URI is
 *     absolute <i>and</i> it has no fragment, or an empty fragment.</li>
 * </ul>
 */

public final class JsonRef
{
    private static final URI HASHONLY_URI = URI.create("#");
    private static final URI EMPTY_URI = URI.create("");

    private static final JsonRef EMPTY;
    private static final LoadingCache<URI, JsonRef> cache;

    static {
        cache = CacheBuilder.newBuilder().maximumSize(15L)
            .build(new CacheLoader<URI, JsonRef>()
            {
                @Override
                public JsonRef load(final URI key)
                    throws Exception
                {
                    return new JsonRef(key);
                }
            });
        EMPTY = new JsonRef(EMPTY_URI);
    }

    /**
     * The URI, as provided by the input arguments
     */
    private final URI uri;

    private final URI locator;
    private final JsonFragment fragment;

    /**
     * The main constructor, which is private by design
     *
     * @param uri Input URI
     */
    private JsonRef(final URI uri)
    {
        try {
            locator = new URI(uri.getScheme(), uri.getSchemeSpecificPart(), "");
            this.uri = uri.getFragment() == null ? locator : uri;
            fragment = JsonFragment.fromFragment(uri.getFragment());
        } catch (URISyntaxException e) {
            throw new RuntimeException("WTF??", e);
        }
    }

    public static JsonRef fromURI(final URI uri)
    {
        Preconditions.checkNotNull(uri, "uri must not be null");

        if (EMPTY_URI.equals(uri) || HASHONLY_URI.equals(uri))
            return EMPTY;

        return cache.getUnchecked(uri.normalize());
    }

    public static JsonRef fromString(final String s)
        throws JsonSchemaException
    {
        Preconditions.checkNotNull(s, "string must not be null");

        if (s.isEmpty() || "#".equals(s))
            return EMPTY;

        try {
            return fromURI(new URI(s));
        } catch (URISyntaxException e) {
            throw new JsonSchemaException("invalid URI: " + s, e);
        }
    }

    public static JsonRef emptyRef()
    {
        return EMPTY;
    }

    public boolean isEmpty()
    {
        return this == EMPTY;
    }

    public boolean isAbsolute()
    {
        return uri.isAbsolute() && fragment.isEmpty();
    }

    public JsonRef resolve(final JsonRef other)
    {
        return new JsonRef(uri.resolve(other.uri));
    }

    /**
     * Return the absolute part of the underlying URI, without the fragment
     *
     * @return an URI
     */
    public URI getRootAsURI()
    {
        return locator;
    }

    /**
     * Return this ref's fragment part as a string
     *
     * <p>If there is no fragment, an empty string is returned.</p>
     *
     * @return the fragment
     */
    public JsonFragment getFragment()
    {
        return fragment;
    }

    public boolean contains(final JsonRef other)
    {
        return locator.equals(other.locator);
    }

    @Override
    public int hashCode()
    {
        return uri.hashCode();
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj == null)
            return false;
        if (this == obj)
            return true;

        if (getClass() != obj.getClass())
            return false;

        final JsonRef that = (JsonRef) obj;
        return uri.equals(that.uri);
    }

    @Override
    public String toString()
    {
        return uri.toString();
    }
}
