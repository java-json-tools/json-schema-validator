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
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;

/**
 * First validator in the validation chain
 *
 * <p>This validator is in charge of resolving JSON References. In most cases,
 * it will not do anything since most schemas are not JSON References.</p>
 *
 * <p>This is also the class which detects ref loops.</p>
 *
 * <p>Its {@link #next()} method always returns a {@link SyntaxJsonValidator}.
 * </p>
 */
public final class RefResolverJsonValidator
    extends JsonValidator
{
    private JsonNode targetSchema;

    public RefResolverJsonValidator(final JsonSchemaFactory factory,
        final JsonNode schema)
    {
        super(factory, schema);
        targetSchema = schema;
    }

    @Override
    public boolean validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        if (!schema.has("$ref"))
            return true;

        final SchemaNode schemaNode
            = new SchemaNode(context.getContainer(), schema);

        try {
            final SchemaNode targetNode
                = validationContext.resolve(schemaNode);
            context.setContainer(targetNode.getContainer());
            targetSchema = targetNode.getNode();
            return true;
        } catch (JsonSchemaException e) {
            report.addMessage(e.getMessage());
            return false;
        }
    }

    @Override
    public JsonValidator next()
    {
        return new SyntaxJsonValidator(factory, targetSchema);
    }
}
