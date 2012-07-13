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

package org.eel.kitchen.jsonschema.schema;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.ValidationContext;
import org.eel.kitchen.jsonschema.util.CollectionUtils;
import org.eel.kitchen.jsonschema.util.JsonPointer;
import org.eel.kitchen.jsonschema.validator.JsonValidator;

import java.util.Set;

public final class ValidJsonSchema
    implements JsonSchema
{
    private final JsonSchemaFactory factory;
    private final JsonValidator validator;
    private final SchemaContainer container;
    private final SchemaNode schemaNode;

    public ValidJsonSchema(final JsonSchemaFactory factory,
        final SchemaContainer container, final SchemaNode schemaNode)
    {
        this.factory = factory;
        validator = factory.getValidatorFactory()
            .fromNode(schemaNode.getNode());
        this.container = container;
        this.schemaNode = schemaNode;
    }

    @Override
    public void validate(final ValidationContext ctx, final JsonNode instance)
    {
        ctx.setContainer(container);

        validator.validate(ctx, instance);

        if (!ctx.isSuccess())
            return;

        if (!instance.isContainerNode())
            return;

        if (instance.isArray())
            validateArray(ctx, instance);
        else
            validateObject(ctx, instance);
    }

    private void validateArray(final ValidationContext ctx,
        final JsonNode instance)
    {
        JsonNode node;
        JsonSchema subSchema;
        final JsonPointer cwd = ctx.getPath();
        JsonPointer ptr;
        int idx = 0;

        for (final JsonNode element: instance) {
            node = schemaNode.getArraySchema(idx);
            ptr = cwd.append(idx);
            ctx.setPath(ptr);
            subSchema = factory.create(container, node);
            subSchema.validate(ctx, element);
            idx++;
        }

        ctx.setPath(cwd);
    }

    private void validateObject(final ValidationContext ctx,
        final JsonNode instance)
    {
        final Set<String> fieldNames
            = CollectionUtils.toSet(instance.fieldNames());
        final JsonPointer cwd = ctx.getPath();

        Set<JsonNode> nodeSet;
        JsonSchema subSchema;
        JsonNode element;
        JsonPointer ptr;

        for (final String field: fieldNames) {
            element = instance.get(field);
            nodeSet = schemaNode.getObjectSchemas(field);
            ptr = cwd.append(field);
            ctx.setPath(ptr);
            for (final JsonNode node: nodeSet) {
                subSchema = factory.create(container, node);
                subSchema.validate(ctx, element);
            }
        }

        ctx.setPath(cwd);
    }
}
