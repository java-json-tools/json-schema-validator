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
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.syntax.draftv4;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.util.NodeType;

public final class RequiredSyntaxValidator
    extends SyntaxValidator
{
    private static final RequiredSyntaxValidator instance
        = new RequiredSyntaxValidator();

    private RequiredSyntaxValidator()
    {
        super("required", NodeType.ARRAY);
    }

    public static RequiredSyntaxValidator getInstance()
    {
        return instance;
    }

    @Override
    protected void checkFurther(final JsonNode schema,
        final ValidationReport report)
        throws JsonValidationFailureException
    {
        final JsonNode node = schema.get(keyword);

        int i = -1;

        for (final JsonNode element: node) {
            i++;
            if (element.isTextual())
                continue;
            report.fail(String.format("array element %d is not a property name",
                i));
        }
    }
}
