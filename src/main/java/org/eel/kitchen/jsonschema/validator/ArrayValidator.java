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
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import org.eel.kitchen.jsonschema.ref.JsonPointer;
import org.eel.kitchen.jsonschema.ref.SchemaNode;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.JacksonUtils;
import org.eel.kitchen.jsonschema.util.NodeAndPath;

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
    private final SchemaNode schemaNode;
    private final JsonNode itemsSchema;
    private final List<JsonNode> tuples;
    private final boolean tupleValidation;
    private final boolean computedItems;
    private final boolean computedAdditional;

    ArrayValidator(final SchemaNode schemaNode)
    {
        this.schemaNode = schemaNode;

        final JsonNode schema = schemaNode.getNode();

        JsonNode node;

        node = schema.path("items");


        if (!node.isArray()) {
            computedAdditional = true; // in fact, we don't care
            tupleValidation = false;
            computedItems = !node.isObject();
            itemsSchema = computedItems ? JacksonUtils.emptySchema() : node;
            tuples = Collections.emptyList();
            return;
        }

        tupleValidation = true;
        computedItems = false;
        tuples = ImmutableList.copyOf(node);

        node = schema.path("additionalItems");

        computedAdditional = !node.isObject();
        itemsSchema = computedAdditional ? JacksonUtils.emptySchema() : node;
    }

    @Override
    public void validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        final JsonPointer pwd = report.getPath();

        JsonNode subSchema, element;
        JsonValidator validator;

        for (int i = 0; i < instance.size(); i++) {
            report.setPath(pwd.append(i));
            element = instance.get(i);
            subSchema = getSchema(i).getNode();
            validator = context.newValidator(subSchema);
            validator.validate(context, report, element);
            if (report.hasFatalError())
                break;
        }

        report.setPath(pwd);
    }

    @VisibleForTesting
    NodeAndPath getSchema(final int index)
    {
        if (!tupleValidation)
            return new NodeAndPath(itemsSchema, "/items", computedItems);

        return index < tuples.size()
            ? new NodeAndPath(tuples.get(index), "/items/" + index)
            : new NodeAndPath(itemsSchema, "/additionalItems",
                computedAdditional);
    }
}
