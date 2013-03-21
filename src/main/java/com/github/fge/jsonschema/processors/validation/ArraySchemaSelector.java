/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.jsonschema.processors.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.google.common.collect.ImmutableList;

import java.util.Collections;

/**
 * JSON Schema subschema selector for array instances
 *
 * <p>Its role is to select which subschemas apply to a given array index of an
 * instance, given a digest built by {@link ArraySchemaDigester}.</p>
 *
 * <p>It may happen that no schemas apply at all (in which case the document at
 * the given array index will always validate successfully).</p>
 */
public final class ArraySchemaSelector
{
    private static final JsonPointer ITEMS = JsonPointer.of("items");
    private static final JsonPointer ADDITIONAL_ITEMS
        = JsonPointer.of("additionalItems");

    private final boolean hasItems;
    private final boolean itemsIsArray;
    private final int itemsSize;
    private final boolean hasAdditional;

    public ArraySchemaSelector(final JsonNode digest)
    {
        hasItems = digest.get("hasItems").booleanValue();
        itemsIsArray = digest.get("itemsIsArray").booleanValue();
        itemsSize = digest.get("itemsSize").intValue();
        hasAdditional = digest.get("hasAdditional").booleanValue();
    }

    public Iterable<JsonPointer> selectSchemas(final int index)
    {
        if (!hasItems)
            return hasAdditional
                ? ImmutableList.of(ADDITIONAL_ITEMS)
                : Collections.<JsonPointer>emptyList();

        if (!itemsIsArray)
            return ImmutableList.of(ITEMS);

        if (index < itemsSize)
            return ImmutableList.of(ITEMS.append(index));

        return hasAdditional
            ? ImmutableList.of(ADDITIONAL_ITEMS)
            : Collections.<JsonPointer>emptyList();
    }
}
