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

import java.util.EnumSet;

public final class DisallowValidator
    extends TypeKeywordValidator
{
    public DisallowValidator(final ValidationContext context,
        final JsonNode instance)
    {
        super(context, instance, "disallow");
    }

    @Override
    public ValidationReport validate()
    {
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

        if (failure) {
            schemas.clear();
            return report;
        }

        buildQueue();

        boolean matchFound = false;

        while (!matchFound && hasMoreElements())
            matchFound = nextElement().validate().isSuccess();

        queue.clear();

        if (matchFound)
            report.addMessage("instance validates against an explicitly "
                + "disallowed schema");

        return report;
    }
}
