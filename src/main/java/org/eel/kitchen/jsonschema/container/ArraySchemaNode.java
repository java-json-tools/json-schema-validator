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

package org.eel.kitchen.jsonschema.container;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for array validation
 *
 * <p>Its role is to provide schemas for array instance elements to an
 * {@link ArrayValidator}. For an array index {@code i} in the instance,
 * the rules are as follows:</p>
 * <ul>
 *     <li>if {@code items} exists and is an object,
 *     then its content is returned;</li>
 *     <li>if {@code items} exists and is an array, and {@code i} is a valid
 *     index for this array, return the corresponding element;
 *     </li>
 *     <li>otherwise, return the contents of {@code additionalItems},
 *     if it is an object, or if undefined, an empty schema.</li>
 * </ul>
 *
 * @see ArrayValidator
 */
public final class ArraySchemaNode
{
    private static final JsonNode EMPTY_SCHEMA
        = JsonNodeFactory.instance.objectNode();

    /**
     * The contents of {@code items}
     */
    private final List<JsonNode> items = new ArrayList<JsonNode>();

    /**
     * The contents of {@code additionalItems}
     */
    private JsonNode additionalItems = EMPTY_SCHEMA;

    public ArraySchemaNode(final JsonNode schema)
    {
        setupArrayNodes(schema);
    }

    private void setupArrayNodes(final JsonNode schema)
    {
        JsonNode node = schema.path("items");

        /**
         * We don't bother at this point: if items is a schema,
         * then it will be used for each and every element of the instance to
         * validate -- it's just as if additionalItems were never defined.
         * So, as items is defined as a list above, we just leave it empty
         * and assign the contents of the keyword to additionalItems.
         */
        if (node.isObject()) {
            additionalItems = node;
            return;
        }

        if (node.isArray())
            for (final JsonNode item: node)
                items.add(item);

        node = schema.path("additionalItems");

        if (node.isObject())
            additionalItems = node;
    }

    public JsonNode arrayPath(final int index)
    {
        return index < items.size() ? items.get(index) : additionalItems;
    }
}
