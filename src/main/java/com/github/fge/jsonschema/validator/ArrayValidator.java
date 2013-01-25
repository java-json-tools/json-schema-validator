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

package com.github.fge.jsonschema.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.report.ValidationReport;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.List;

/**
 * Validator called for array instance children
 *
 * <p>Array children must obey one schema, depending on the values of the
 * {@code items} and {@code additionalItems} schema keywords. No {@code items}
 * is equivalent to this keyword having an empty schema.</p>
 *
 * <p>Array indices start at 0. For a given index:</p>
 *
 * <ul>
 *     <li>if {@code items} is a schema, this schema matches all elements;</li>
 *     <li>if there is a schema at this index in {@code items}, there is a
 *     match;</li>
 *     <li>otherwise, {@code additionalItems} is considered (if {@code true} or
 *     nonexistent, then an empty schema).</li>
 * </ul>
 *
 */
final class ArrayValidator
    implements JsonValidator
{
    private final JsonNode additionalItems;

    private final List<JsonNode> items;

    ArrayValidator(final JsonNode schema)
    {
        JsonNode node;

        node = schema.path("items");

        if (!node.isArray()) {
            // Either it is missing or it is an object
            additionalItems = node.isObject() ? node
                : JacksonUtils.emptyObject();
            items = Collections.emptyList();
            return;
        }

        items = ImmutableList.copyOf(node);

        node = schema.path("additionalItems");

        additionalItems = node.isObject() ? node : JacksonUtils.emptyObject();

    }

    @Override
    public void validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        JsonNode subSchema, element;
        JsonValidator validator;

        for (int i = 0; i < instance.size(); i++) {
            report.pushd(i);
            element = instance.get(i);
            subSchema = getSchema(i);
            validator = context.newValidator(subSchema);
            validator.validate(context, report, element);
            report.popd();
            if (report.hasFatalError())
                break;
        }
    }

    @VisibleForTesting
    JsonNode getSchema(final int index)
    {
        return index >= items.size() ? additionalItems : items.get(index);
    }
}
