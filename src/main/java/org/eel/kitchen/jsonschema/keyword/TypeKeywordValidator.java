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
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.main.SchemaContainer;
import org.eel.kitchen.jsonschema.main.SchemaNode;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.validator.JsonValidator;
import org.eel.kitchen.jsonschema.validator.RefResolverJsonValidator;

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
        final ValidationReport report, final JsonNode instance)
    {
        if (typeSet.contains(NodeType.getNodeType(instance)))
            return;

        final ValidationReport typeReport = report.copy();

        typeReport.addMessage("instance does not match any allowed primitive "
            + "type");

        final SchemaContainer container = context.getContainer();
        final JsonSchemaFactory factory = context.getFactory();

        ValidationReport tempReport;
        SchemaNode subNode;
        JsonValidator validator;

        for (final JsonNode schema: schemas) {
            tempReport = report.copy();
            subNode = new SchemaNode(container, schema);
            validator = new RefResolverJsonValidator(factory, subNode);
            while (validator.validate(context, tempReport, instance))
                validator = validator.next();
            context.setContainer(container);
            if (tempReport.isSuccess())
                return;
            typeReport.mergeWith(tempReport);
        }

        report.mergeWith(typeReport);
    }
}
