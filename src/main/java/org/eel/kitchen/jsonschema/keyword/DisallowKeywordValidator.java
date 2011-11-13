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
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.jsonschema.context.ValidationContext;
import org.eel.kitchen.util.NodeType;

import java.util.EnumSet;
import java.util.List;

/**
 * Validator for the {@code disallow} keyword (draft section 5.25)
 *
 * @see AbstractTypeKeywordValidator
 */
public final class DisallowKeywordValidator
    extends AbstractTypeKeywordValidator
{
    public DisallowKeywordValidator()
    {
        super("disallow");
    }

    @Override
    protected ValidationReport doValidate(final ValidationContext context,
        final JsonNode instance, final EnumSet<NodeType> typeSet,
        final List<JsonNode> schemas)
    {
        final ValidationReport report = context.createReport();

        final NodeType type = NodeType.getNodeType(instance);

        boolean failure = false;

        if (typeSet.containsAll(EnumSet.allOf(NodeType.class))) {
            failure = true;
            report.addMessage("disallow keyword forbids all primitive types, "
                + "validation will always fail!");
        } else if (typeSet.contains(type)) {
            failure = true;
            report.addMessage(String.format("instance is of type %s, "
                + "which falls into the list of explicitly disallowed types "
                + "(%s)", type, typeSet));
        }

        if (failure)
            return report;

        ValidationReport schemaReport;

        for (final JsonNode schema: schemas) {
            schemaReport = validateSchema(context, schema, instance);
            if (schemaReport.isSuccess()) {
                report.addMessage("instance validates against an explicitly "
                    + "disallowed schema");
                break;
            }
            if (schemaReport.isError()) {
                report.mergeWith(schemaReport);
                break;
            }
        }

        return report;
    }
}
