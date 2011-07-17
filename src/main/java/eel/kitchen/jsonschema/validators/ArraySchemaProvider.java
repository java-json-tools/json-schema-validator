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

import eel.kitchen.jsonschema.validators.type.ArrayValidator;
import org.codehaus.jackson.JsonNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Schema provider for JSON nodes of type array. This type of schema
 * provider is spawned from an {@link ArrayValidator}.</p>
 *
 * @see {@link SchemaProvider}
 * @see {@link ArrayValidator}
 */
public final class ArraySchemaProvider
    implements SchemaProvider
{
    /**
     * A map of schemas found by {@link ArrayValidator} in the items property
     * of a JSON schema, with keys being the index in the items array as
     * strings. It will be non empty only if item tuple validation is in effect.
     */
    private final Map<String, JsonNode> items
        = new HashMap<String, JsonNode>();

    /**
     * The value for additionalItems, or items if tuple validation is not in
     * effect.
     */
    private final JsonNode additionalItems;

    /**
     * Constructor.
     *
     * @param itemList list of schemas in the items property if it is an array,
     * in the order of elements in this array
     * @param additionalItems schema for all array elements not in items
     */
    public ArraySchemaProvider(final List<JsonNode> itemList,
        final JsonNode additionalItems)
    {
        int i = 0;

        for (final JsonNode item: itemList)
            items.put(Integer.toString(i++), item);

        this.additionalItems = additionalItems;
    }

    /**
     * Get the schema associated with the given path. Returns the
     * corresponding element in items if found, otherwise additionalItems.
     *
     * @param path The subpath
     * @return the matching schema
     */
    @Override
    public JsonNode getSchemaForPath(final String path)
    {
        return items.containsKey(path) ? items.get(path) : additionalItems;
    }
}
