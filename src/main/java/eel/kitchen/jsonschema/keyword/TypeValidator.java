/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
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

/**
 * Keyword validator for the {@code type} keyword (section 5.1)
 *
 * @see {@link TypeKeywordValidator}
 */
public final class TypeValidator
    extends TypeKeywordValidator
{
    public TypeValidator(final ValidationContext context,
        final JsonNode instance)
    {
        super(context, instance, "type");
    }

    /**
     * <p>Validate the instance:</p>
     * <ul>
     *     <li>if the type of the instance is one of the registered primitive
     *     types, validation succeeds;</li>
     *     <li>otherwise, try and match enclosed schemas if any: if only one
     *     matches, we have a success.</li>
     * </ul>
     * @return the validation report
     */
    @Override
    public ValidationReport validate()
    {
        final NodeType type = NodeType.getNodeType(instance);

        String message = "cannot match anything! Empty simple type set "
                + "_and_ I don't have any enclosed schema either";

        if (schemas.isEmpty() && typeSet.isEmpty()) {
            report.addMessage(message);
            return report;
        }

        if (typeSet.contains(type)) {
            schemas.clear();
            return report;
        }

        message = typeSet.isEmpty() ? "no primitive types to match against"
            : String.format("instance is of type %s, which is none of "
                + "the allowed primitive types (%s)", type, typeSet);


        report.addMessage(message);

        buildQueue();

        if (!hasMoreElements())
            return report;

        report.addMessage("trying with enclosed schemas instead");

        for (int i = 1; hasMoreElements(); i++) {
            report.addMessage("trying schema #" + i + "...");
            final ValidationReport tmp = nextElement().validate();
            if (tmp.isSuccess())
                return tmp;
            report.mergeWith(tmp);
            report.addMessage("schema #" + i + ": no match");
        }

        report.addMessage("enclosed schemas did not match");

        return report;
    }
}
