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
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.schema.JsonSchema;
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
    public void validate(final ValidationReport report,
        final JsonNode instance)
    {
        if (typeSet.contains(NodeType.getNodeType(instance)))
            return;

        final JsonPointer path = report.getPath();
        final ValidationReport fullReport = new ValidationReport(path);

        fullReport.addMessage("instance does not match any allowed primitive "
            + "type");

        ValidationReport schemaReport;

        for (final JsonNode schema: schemas) {
            schemaReport = new ValidationReport(path);
            JsonSchema.fromNode(report.getSchema(), schema)
                .validate(schemaReport, instance);
            if (schemaReport.isSuccess())
                return;
            fullReport.mergeWith(schemaReport);
        }

        report.mergeWith(fullReport);
    }
}
