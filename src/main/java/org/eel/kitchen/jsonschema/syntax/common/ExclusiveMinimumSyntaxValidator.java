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

package org.eel.kitchen.jsonschema.syntax.common;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.util.NodeType;

public final class ExclusiveMinimumSyntaxValidator
    extends SyntaxValidator
{
    private static final ExclusiveMinimumSyntaxValidator instance
        = new ExclusiveMinimumSyntaxValidator();

    public static ExclusiveMinimumSyntaxValidator getInstance()
    {
        return instance;
    }

    private ExclusiveMinimumSyntaxValidator()
    {
        super("exclusiveMinimum", NodeType.BOOLEAN);
    }

    /**
     * Check that {@code exclusiveMinimum} is paired with {@code minimum}
     */
    @Override
    protected void checkFurther(final JsonNode schema,
        final ValidationReport report)
        throws JsonValidationFailureException
    {
        if (!schema.has("minimum"))
            report.fail("exclusiveMinimum without minimum");
    }
}
