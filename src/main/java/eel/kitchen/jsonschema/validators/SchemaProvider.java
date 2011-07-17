/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.validators;

import eel.kitchen.jsonschema.SchemaNode;
import eel.kitchen.util.IterableJsonNode;
import org.codehaus.jackson.JsonNode;

/**
 * <p>Provide schemas for individual elements of container JSON nodes
 * (arrays, objects). Should be paired with an {@link IterableJsonNode} for
 * safety. Never goes more than one level deep.
 * </p>
 *
 * @see {@link IterableJsonNode}, {@link SchemaNode}
 */

public interface SchemaProvider
{
    /**
     * <p>Get a subschema in the shape of a {@link JsonNode} for the given
     * path specification. See sections 6.2.1 and 6.2.2 of the JSON Schema
     * draft specification. For arrays, this is a string containing the index
     * within the array, for an object this is the URL encoded name of the
     * property (think spaces).</p>
     *
     * @param path The subpath
     * @return the schema associated with this subpath
     */
    JsonNode getSchemaForPath(final String path);
}
