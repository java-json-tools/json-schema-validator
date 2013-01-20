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

package com.github.fge.jsonschema.ref;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;

/**
 * An illegal fragment
 *
 * <p>Looking up such a fragment always fails.</p>
 *
 * <p>This will be spawned in the event when canonical addressing mode is used
 * and the fragment part is not a JSON Pointer.</p>
 *
 * @see JsonPointer
 */
final class IllegalFragment
    extends JsonFragment
{
    IllegalFragment(final String id)
    {
        super(id);
    }

    @Override
    public JsonNode resolve(final JsonNode node)
    {
        return MissingNode.getInstance();
    }
}
