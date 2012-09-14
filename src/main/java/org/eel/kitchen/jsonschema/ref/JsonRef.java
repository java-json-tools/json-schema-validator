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
import com.google.common.base.Preconditions;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.report.Domain;
import org.eel.kitchen.jsonschema.report.Message;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Representation of a JSON Reference
 *
 * <p><a href="http://tools.ietf.org/html/draft-pbryan-zyp-json-ref-03">JSON
 * Reference</a>, currently a draft, is a way to define a path within a JSON
 * document.</p>
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
 * <p>This class is used in a more general way than the draft. It is also used
 * as a backing class for schema identifiers.</p>
 *
 * <p>The implementation is a wrapper over Java's {@link URI}, with the
 * following differences:</p>
 *
 * <ul>
 *     <li>all URIs are normalized from the get go;</li>
 *     <li>an empty fragment is equivalent to no fragment at all, and stands for
 *     a root JSON Pointer;</li>
 *     <li>a reference is taken to be absolute if the underlying URI is
 *     absolute <i>and</i> it has no fragment, or an empty fragment.</li>
 * </ul>
 *
 * <p>This class is thread safe and immutable.</p>
 */

public abstract class JsonRef
{
    private static final URI EMPTY_URI = URI.create("");

    protected static final URI HASHONLY_URI = URI.create("#");

    /**
     * The URI, as provided by the input, with an appended empty fragment if
     * no fragment was provided
     */
    protected final URI uri;
    protected final String asString;
    protected final URI locator;
    protected final JsonFragment fragment;
    protected final int hashCode;

    protected JsonRef(final URI uri)
    {
        final String scheme = uri.getScheme();
        final String ssp = uri.getSchemeSpecificPart();
        final String uriFragment = uri.getFragment();

        final String realFragment = uriFragment == null ? "" : uriFragment;

        try {
            this.uri = new URI(scheme, ssp, realFragment);
            locator = new URI(scheme, ssp, "");
            fragment = JsonFragment.fromFragment(realFragment);
            asString = this.uri.toString();
            hashCode = asString.hashCode();
        } catch (URISyntaxException e) {
            throw new RuntimeException("WTF??", e);
        }
    }
    /**
     * Build a JSON Reference from a URI
     *
     * @param uri the provided URI
     * @return the JSON Reference
     * @throws NullPointerException the provided URI is null
     */
    public static JsonRef fromURI(final URI uri)
    {
        Preconditions.checkNotNull(uri, "URI must not be null");

        final URI normalized = uri.normalize();

        if (HASHONLY_URI.equals(normalized) || EMPTY_URI.equals(normalized))
            return EmptyJsonRef.getInstance();

        return "jar".equals(normalized.getScheme())
            ? new JarJsonRef(normalized)
            : new HierarchicalJsonRef(normalized);
    }

    /**
     * Build a JSON Reference from a string input
     *
     * @param s the string
     * @return the reference
     * @throws JsonSchemaException string is not a valid URI
     * @throws NullPointerException provided string is null
     */
    public static JsonRef fromString(final String s)
        throws JsonSchemaException
    {
        Preconditions.checkNotNull(s, "string must not be null");

        try {
            return fromURI(new URI(s));
        } catch (URISyntaxException e) {
            final Message.Builder msg = Domain.REF_RESOLVING.newMessage()
                .setKeyword("N/A").addInfo("uri", s).setMessage("invalid URI");
            throw new JsonSchemaException(msg.build(), e);
        }
    }

    /**
     * Build a JSON Reference from a {@link JsonNode}
     *
     * <p>If the node is not textual, this returns an empty reference.
     * Otherwise, it calls {@link #fromString(String)} with this node's text
     * value.</p>
     *
     * @param node the node
     * @return the reference
     * @throws JsonSchemaException see {@link #fromString(String)}
     * @throws NullPointerException provided node is null
     */
    public static JsonRef fromNode(final JsonNode node)
        throws JsonSchemaException
    {
        Preconditions.checkNotNull(node, "node must not be null");

        return node.isTextual() ? fromString(node.textValue())
            : EmptyJsonRef.getInstance();
    }

    /**
     * Return an empty reference
     *
     * <p>An empty reference is a reference which only has an empty fragment.
     * </p>
     *
     * @return see above
     */
    public static JsonRef emptyRef()
    {
        return EmptyJsonRef.getInstance();
    }

    public final URI toURI()
    {
        return uri;
    }

    /**
     * Tell whether this reference is an absolute reference
     *
     * <p>A JSON Reference is considered absolute iif the underlying URI is
     * itself absolute <b>and</b> it has an empty, or no, fragment part.</p>
     *
     * @return see above
     */
    public abstract boolean isAbsolute();

    /**
     * Resolve this reference against another reference
     *
     * @param other the reference to resolve
     * @return the resolved reference
     */
    public abstract JsonRef resolve(final JsonRef other);

    /**
     * Return this JSON Reference's locator
     *
     * <p>The locator of a fragment is the URI with an empty fragment.</p>
     *
     * @return an URI
     */
    public final URI getLocator()
    {
        return locator;
    }

    /**
     * Return this JSON Reference's fragment
     *
     * @return the fragment
     */
    public final JsonFragment getFragment()
    {
        return fragment;
    }

    /**
     * Tell whether the current JSON Reference contains another
     *
     * <p>This is considered true iif both references have the same locator,
     * in other words, if they differ only by their fragment part.</p>
     *
     * @param other the other reference
     * @return see above
     */
    public final boolean contains(final JsonRef other)
    {
        return locator.equals(other.locator);
    }

    @Override
    public final int hashCode()
    {
        return hashCode;
    }

    @Override
    public final boolean equals(final Object o)
    {
        if (o == null)
            return false;
        if (this == o)
            return true;

        if (!(o instanceof JsonRef))
            return false;

        final JsonRef that = (JsonRef) o;
        return asString.equals(that.asString);
    }

    @Override
    public final String toString()
    {
        return asString;
    }
}
