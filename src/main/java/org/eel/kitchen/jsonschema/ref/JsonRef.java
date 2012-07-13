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

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.JsonSchemaException;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Representation of a JSON Reference
 *
 * <p><a href="http://tools.ietf
 * .org/html/draft-pbryan-zyp-json-ref-02">JSON Reference</a>,
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
    /**
     * This could theoretically thrown an IllegalArgumentException. Meh.
     */
    private static final URI EMPTY_URI = URI.create("#");

    private static final JsonRef EMPTY = new JsonRef(EMPTY_URI);

    /**
     * The URI, as provided by the input arguments
     */
    private URI uri;

    private boolean normalized;

    /**
     * The main constructor, which is private by design
     *
     * @param uri Input URI
     */
    public JsonRef(final URI uri)
    {
        process(uri);
    }

    private void process(final URI uri)
    {
        URI normalize = uri.normalize();
        normalized = uri.equals(normalize);
        if (normalize.getFragment() == null)
            try {
                normalize = new URI(normalize.getScheme(),
                    normalize.getSchemeSpecificPart(), "");
            } catch (URISyntaxException e) {
                throw new RuntimeException("WTF??", e);
            }

        this.uri = normalize;

    }

    public JsonRef(final String s)
        throws JsonSchemaException
    {
        try {
            process(new URI(s));
        } catch (URISyntaxException e) {
            throw new JsonSchemaException("invalid URI \"" + s + "\"", e);
        }
    }

    /**
     * Build a JSON Reference.
     *
     * <p>Note that in the event where there is no member by the requested
     * name, an empty JSON Reference is returned.</p>
     *
     * @param node the JSON instance to extract the reference from
     * @param key the member to extract the reference from
     * @return the reference
     * @throws JsonSchemaException the key is malformed (not a string,
     * or not an URI)
     */
    public static JsonRef fromNode(final JsonNode node, final String key)
        throws JsonSchemaException
    {
        final JsonNode entry = node.path(key);
        if (entry.isMissingNode())
            return EMPTY;

        if (!entry.isTextual())
            throw new JsonSchemaException("invalid " + key + " entry: not a "
                + "string");

        return new JsonRef(entry.textValue());
    }

    public boolean isEmpty()
    {
        return uri.equals(EMPTY_URI);
    }

    public boolean isAbsolute()
    {
        return uri.isAbsolute() && !hasFragment();
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
        try {
            return new URI(uri.getScheme(), uri.getSchemeSpecificPart(), "");
        } catch (URISyntaxException e) {
            throw new RuntimeException("WTF???", e);
        }
    }

    /**
     * Return this ref's fragment part as a string
     *
     * <p>If there is no fragment, an empty string is returned.</p>
     *
     * @return the fragment
     */
    public String getFragment()
    {
        return uri.getFragment();
    }

    public boolean hasFragment()
    {
        return !getFragment().isEmpty();
    }

    public boolean isNormalized()
    {
        return normalized;
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
