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

import org.codehaus.jackson.JsonNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ArraySchemaProvider
    implements SchemaProvider
{
    private final Map<String, JsonNode> items
        = new HashMap<String, JsonNode>();

    private final JsonNode additionalItems;

    public ArraySchemaProvider(final List<JsonNode> itemList,
        final JsonNode additionalItems)
    {
        int i = 0;

        for (final JsonNode item: itemList)
            items.put(String.format("[%d]", i++), item);

        this.additionalItems = additionalItems;
    }

    @Override
    public JsonNode getSchemaForPath(final String path)
    {
        return items.containsKey(path) ? items.get(path) : additionalItems;
    }
}
