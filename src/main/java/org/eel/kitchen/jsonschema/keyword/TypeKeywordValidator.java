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
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.schema.AbstractJsonSchema;
import org.eel.kitchen.util.JsonPointer;
import org.eel.kitchen.util.NodeType;

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

        final JsonPointer path = context.getPath();
        final ValidationContext fullContext = new ValidationContext(path);

        fullContext.addMessage("instance does not match any allowed primitive "
            + "type");

        ValidationContext schemaContext;

        for (final JsonNode schema: schemas) {
            schemaContext = new ValidationContext(path);
            AbstractJsonSchema.fromNode(context.getSchema(), schema)
                .validate(schemaContext, instance);
            if (schemaContext.isSuccess())
                return;
            fullContext.mergeWith(schemaContext);
        }

        context.mergeWith(fullContext);
    }
}
