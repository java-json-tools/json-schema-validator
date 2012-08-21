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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.collect.ImmutableList;
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.main.SchemaContainer;
import org.eel.kitchen.jsonschema.main.SchemaNode;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.ref.JsonPointer;

import java.util.Collections;
import java.util.List;

public final class ArrayJsonValidator
    implements JsonValidator
{
    private static final JsonNode EMPTY_SCHEMA
        = JsonNodeFactory.instance.objectNode();

    private final JsonSchemaFactory factory;

    private JsonNode additionalItems = EMPTY_SCHEMA;

    private final List<JsonNode> items;

    ArrayJsonValidator(final JsonSchemaFactory factory,
        final SchemaNode schemaNode)
    {
        this.factory = factory;

        final JsonNode schema = schemaNode.getNode();

        JsonNode node = schema.path("items");

        if (node.isObject()) {
            additionalItems = node;
            items = Collections.emptyList();
            return;
        }

        // We know that if "items" is not an object, it is an array
        items = ImmutableList.copyOf(node);

        node = schema.path("additionalItems");

        if (node.isObject())
            additionalItems = node;
    }

    @Override
    public boolean validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        final SchemaContainer container = context.getContainer();
        final JsonPointer pwd = report.getPath();

        SchemaNode subSchema;
        JsonNode element;
        JsonValidator validator;

        for (int i = 0; i < instance.size(); i++) {
            subSchema = getSchemaNode(container, i);
            element = instance.get(i);
            report.setPath(pwd.append(i));
            validator = new RefResolverJsonValidator(factory, subSchema);
            while (validator.validate(context, report, element))
                validator = validator.next();
            context.setContainer(container);
        }

        report.setPath(pwd);
        return false;
    }

    @Override
    public JsonValidator next()
    {
        return null;
    }

    private SchemaNode getSchemaNode(final SchemaContainer container,
        final int index)
    {
        final JsonNode node = index >= items.size() ? additionalItems
            : items.get(index);

        return new SchemaNode(container, node);
    }
}
