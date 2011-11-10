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

/**
 * Validator for the {@code disallow} keyword (draft section 5.25)
 *
 * @see AbstractTypeKeywordValidator
 */
public final class DisallowKeywordValidator
    extends AbstractTypeKeywordValidator
{
    public DisallowKeywordValidator(final ValidationContext context,
        final JsonNode instance)
    {
        super(context, instance, "disallow");
    }

    /**
     * <p>Validate the instance:</p>
     * <ul>
     *     <li>if the instance type is one registered in {@link #typeSet},
     *     this is a failure;
     *     </li>
     *     <li>otherwise, if any, attempt to match against schema
     *     dependencies: if one match is found, validation is a failure.</li>
     * </ul>
     *
     * @return the validation report
     */
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

        while (!matchFound && hasMoreElements()) {
            final ValidationReport innerReport = nextElement().validate();
            if (innerReport.isError()) {
                report.mergeWith(innerReport);
                return report;
            }
            matchFound = innerReport.isSuccess();
        }

        queue.clear();

        if (matchFound)
            report.addMessage("instance validates against an explicitly "
                + "disallowed schema");

        return report;
    }
}
