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
import com.fasterxml.jackson.databind.node.MissingNode;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;

/**
 * Abstract class for fragment resolution
 *
 * <p>In a JSON Reference, a fragment can either be a JSON Pointer or a schema
 * id. In both cases, when found, the fragment must be resolve with regards to
 * the schema. This is what this class, and its two implementations, are for.
 * </p>
 *
 * @see IdFragment
 * @see JsonPointer
 */
public abstract class JsonFragment
    implements Comparable<JsonFragment>
{
    protected final String asString;

    protected JsonFragment(final String input)
    {
        asString = input;
    }

    /**
     * Special case fragment (empty)
     */
    private static final JsonFragment EMPTY = new JsonFragment("")
    {
        @Override
        public JsonNode resolve(final JsonNode node)
        {
            return node;
        }
    };

    /**
     * The only static factory method to obtain a fragment
     *
     * <p>Depending on the situation, this method will either return a
     * {@link JsonPointer}, an {@link IdFragment} or {@link #EMPTY}.</p>
     *
     * @param fragment the fragment as a string
     * @return the fragment
     */
    public static JsonFragment fromFragment(final String fragment)
    {
        if (fragment.isEmpty())
            return EMPTY;

        try {
            return new JsonPointer(fragment);
        } catch (JsonSchemaException ignored) {
            // Not a valid JSON Pointer: it is an id
            return new IdFragment(fragment);
        }
    }

    /**
     * Resolve this fragment against a given node
     *
     * @param node the node
     * @return the result node ({@link MissingNode} if the fragment is not
     * found)
     */
    public abstract JsonNode resolve(final JsonNode node);

    /**
     * Tell whether this fragment is empty
     *
     * @see JsonRef#isAbsolute()
     * @return true if it is {@link #EMPTY}
     */
    public final boolean isEmpty()
    {
        // This works: we always return EMPTY with a null fragment
        return this == EMPTY;
    }

    @Override
    public int compareTo(final JsonFragment other)
    {
        return asString.compareTo(other.asString);
    }


    @Override
    public final int hashCode()
    {
        return asString.hashCode();
    }

    @Override
    public final boolean equals(final Object obj)
    {
        if (obj == null)
            return false;
        if (!(obj instanceof JsonFragment))
            return false;

        final JsonFragment other = (JsonFragment) obj;

        return asString.equals(other.asString);
    }

    @Override
    public final String toString()
    {
        return asString;
    }
}
