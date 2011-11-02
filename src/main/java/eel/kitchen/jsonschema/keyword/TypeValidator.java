/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.keyword;

import eel.kitchen.jsonschema.ValidationReport;
import eel.kitchen.jsonschema.context.ValidationContext;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

public final class TypeValidator
    extends TypeKeywordValidator
{
    public TypeValidator(final ValidationContext context,
        final JsonNode instance)
    {
        super(context, instance, "type");
    }

    @Override
    public ValidationReport validate()
    {
        final NodeType type = NodeType.getNodeType(instance);

        if (typeSet.contains(type)) {
            schemas.clear();
            return report;
        }

        buildQueue();

        if (!hasMoreElements()) {
            report.addMessage("instance is not of any allowed primitive type");
            return report;
        }

        buildQueue();

        boolean matchFound = false;

        final ValidationReport other = new ValidationReport();

        while (!matchFound && hasMoreElements()) {
            other.mergeWith(nextElement().validate());
            matchFound = other.isSuccess();
        }

        if (!matchFound) {
            report.addMessage("instance is not of any allowed primitive type, "
                + "and no further schema matched it either");
            report.addMessage("Report for other schemas:");
            report.addMessage("---- BEGIN REPORT ----");
            report.mergeWith(other);
            report.addMessage("----  END REPORT  ----");
        }

        return report;
    }

}
