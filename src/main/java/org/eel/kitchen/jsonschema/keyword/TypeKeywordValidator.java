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

package org.eel.kitchen.jsonschema.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.ValidationContext;
import org.eel.kitchen.jsonschema.schema.JsonSchema;
import org.eel.kitchen.jsonschema.schema.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.schema.SchemaContainer;
import org.eel.kitchen.jsonschema.util.NodeType;

/**
 * Validator for the {@code type} keyword
 *
 * <p>This keyword and its counterpart ({@code disallowed}) are two of the
 * most complex keywords.</p>
 */
public final class TypeKeywordValidator
    extends AbstractTypeKeywordValidator
{
    public TypeKeywordValidator(final JsonNode schema)
    {
        super("type", schema);
    }

    @Override
    public void validate(final ValidationContext context,
        final JsonNode instance)
    {
        if (typeSet.contains(NodeType.getNodeType(instance)))
            return;

        final SchemaContainer container = context.getContainer();
        final JsonSchemaFactory factory = context.getFactory();

        final ValidationContext ctx = context.copy();

        ctx.addMessage("instance does not match any allowed primitive type");

        JsonSchema subSchema;
        ValidationContext tmp;

        for (final JsonNode schema: schemas) {
            tmp = ctx.copy();
            subSchema = factory.create(container, schema);
            subSchema.validate(tmp, instance);
            if (tmp.isSuccess())
                return;
            ctx.mergeWith(tmp);
        }

        context.mergeWith(ctx);
    }
}
