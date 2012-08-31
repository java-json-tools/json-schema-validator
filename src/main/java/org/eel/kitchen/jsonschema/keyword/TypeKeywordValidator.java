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
import org.eel.kitchen.jsonschema.report.ValidationMessage;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.validator.JsonValidator;
import org.eel.kitchen.jsonschema.validator.ValidationContext;

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
        final NodeType type = NodeType.getNodeType(instance);
        if (typeSet.contains(type))
            return;

        final ValidationReport schemaReport = report.copy();

        if (!schemas.isEmpty()) {
            trySchemas(context, schemaReport, instance);
            if (schemaReport.isSuccess())
                return;
        }

        /*
         * We must do that test here. Remember that the schema mandates a
         * type array, if it is an array, to have at least one item. If we come
         * to this point, it means that one element was either a primitive type
         * or a schema -- understand: there may not have been a primitive type
         * at all in the array.
         *
         * So, in order to avoid error messages of the type "instance type is
         * not allowed" with an empty type set, we only add the primitive type
         * mismatch message if there was at least one primitive type in the set.
         */
        if (!typeSet.isEmpty()) {
            final ValidationMessage.Builder msg = newMsg()
                .addInfo("found", type).addInfo("allowed", typeSet)
                .setMessage("instance does not match any allowed primitive type");
            report.addMessage(msg.build());
        }

        report.mergeWith(schemaReport);
    }

    private void trySchemas(final ValidationContext context,
        final ValidationReport schemaReport, final JsonNode instance)
    {
        final ValidationReport report = schemaReport.copy();

        ValidationReport subReport;
        JsonValidator validator;

        for (final JsonNode schema: schemas) {
            subReport = report.copy();
            validator = context.newValidator(schema);
            validator.validate(context, subReport, instance);
            if (subReport.isSuccess())
                return;
            report.mergeWith(subReport);
        }

        schemaReport.mergeWith(report);
    }
}
