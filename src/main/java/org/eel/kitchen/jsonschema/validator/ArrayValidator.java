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

package org.eel.kitchen.jsonschema.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import org.eel.kitchen.jsonschema.main.SchemaContainer;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.ref.JsonPointer;

import java.util.Collections;
import java.util.List;

/**
 * Validator called for array instance children
 *
 * <p>Array children must obey one schema, depending on the values of the
 * {@code items} and {@code additionalItems} schema keywords. No {@code items}
 * is equivalent to an empty array.</p>
 *
 * <p>Array indices start at 0. For a given index:</p>
 *
 * <ul>
 *     <li>if there is a schema at this index in {@code items}, there is a
 *     match;</li>
 *     <li>otherwise, {@code additionalItems} is considered (if {@code true} or
 *     nonexistent, then an empty schema).</li>
 * </ul>
 *
 * <p>Its {@link #next()} method should <b>never</b> be called (it throws an
 * {@link IllegalStateException} if it is).</p>
 */
final class ArrayValidator
    extends ContainerValidator
{
    private final JsonNode additionalItems;

    private final List<JsonNode> items;

    ArrayValidator(final JsonValidatorCache cache, final SchemaNode schemaNode)
    {
        super(cache, schemaNode);

        JsonNode node;

        node = schema.path("items");

        if (node.isObject()) {
            additionalItems = node;
            items = Collections.emptyList();
            return;
        }

        // We know that if "items" is not an object, it is an array
        items = ImmutableList.copyOf(node);

        node = schema.path("additionalItems");

        additionalItems = node.isObject() ? node : EMPTY_SCHEMA;
    }

    @Override
    public boolean validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        final SchemaContainer orig = context.getContainer();
        final JsonPointer pwd = report.getPath();

        JsonNode element;
        JsonValidator validator;
        SchemaNode subNode;

        for (int i = 0; i < instance.size(); i++) {
            subNode = new SchemaNode(orig, getSchema(i));
            element = instance.get(i);
            report.setPath(pwd.append(i));
            validator = cache.getValidator(subNode);
            validator.validate(context, report, element);
            context.setContainer(orig);
        }

        report.setPath(pwd);
        return false;
    }

    @Override
    public JsonValidator next()
    {
        throw new IllegalStateException("I should not have been called");
    }

    private JsonNode getSchema(final int index)
    {
        return index >= items.size() ? additionalItems : items.get(index);
    }
}
