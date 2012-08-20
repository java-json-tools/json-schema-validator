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
import org.eel.kitchen.jsonschema.main.JsonSchemaException;

public abstract class JsonFragment
{
    private static final JsonFragment EMPTY = new JsonFragment()
    {
        @Override
        public JsonNode resolve(final JsonNode node)
        {
            return node;
        }

        @Override
        public String toString()
        {
            return "#";
        }
    };

    public static JsonFragment fromFragment(final String fragment)
    {
        if (fragment == null || fragment.isEmpty())
            return EMPTY;

        try {
            return new JsonPointer(fragment);
        } catch (JsonSchemaException ignored) {
            // Not a valid JSON Pointer: it is an id
            return new IdFragment(fragment);
        }
    }

    public abstract JsonNode resolve(final JsonNode node);

    public final boolean isEmpty()
    {
        // This works: we always return EMPTY with a null fragment
        return this == EMPTY;
    }

    @Override
    public abstract String toString();
}
