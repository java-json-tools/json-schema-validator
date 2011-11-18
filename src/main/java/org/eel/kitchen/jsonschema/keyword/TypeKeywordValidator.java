/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.keyword;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.util.NodeType;

import java.util.EnumSet;
import java.util.List;

/**
 * Keyword validator for the {@code type} keyword (section 5.1)
 *
 * @see AbstractTypeKeywordValidator
 */
public final class TypeKeywordValidator
    extends AbstractTypeKeywordValidator
{
    public TypeKeywordValidator()
    {
        super("type");
    }

    @Override
    protected ValidationReport doValidate(final ValidationContext context,
        final JsonNode instance, final EnumSet<NodeType> typeSet,
        final List<JsonNode> schemas)
    {
        final ValidationReport report = context.createReport();
        final NodeType type = NodeType.getNodeType(instance);

        if (typeSet.contains(type))
            return report;

        String message = "cannot match anything! Empty simple type set "
            + "_and_ I don't have any enclosed schema either";

        if (schemas.isEmpty() && typeSet.isEmpty()) {
            report.addMessage(message);
            return report;
        }

        message = typeSet.isEmpty() ? "no primitive types to match against"
            : String.format("instance is of type %s, which is none of "
                + "the allowed primitive types (%s)", type, typeSet);


        report.addMessage(message);

        if (schemas.isEmpty())
            return report;

        report.addMessage("trying with enclosed schemas instead");

        int i = 1;
        ValidationReport schemaReport;

        for (final JsonNode schema: schemas) {
            report.addMessage("trying schema #" + i + "...");
            schemaReport = validateSchema(context, schema, instance);
            if (schemaReport.isSuccess())
                return schemaReport;
            report.mergeWith(schemaReport);
            report.addMessage("schema #" + i + ": no match");
            i++;
        }

        report.addMessage("enclosed schemas did not match");

        return report;
    }
}
