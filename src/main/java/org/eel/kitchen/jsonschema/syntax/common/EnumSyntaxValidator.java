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

package org.eel.kitchen.jsonschema.syntax.common;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.util.NodeType;

import java.util.HashSet;
import java.util.Set;

public final class EnumSyntaxValidator
    extends SyntaxValidator
{
    private static final EnumSyntaxValidator instance
        = new EnumSyntaxValidator();

    public static EnumSyntaxValidator getInstance()
    {
        return instance;
    }

    private EnumSyntaxValidator()
    {
        super("enum", NodeType.ARRAY);
    }

    /**
     * Abstract method for validators which need to check more than the type
     * of the node to validate
     *
     * @param schema the schema to analyze
     * @param report the report to use
     */
    @Override
    protected void checkFurther(final JsonNode schema,
        final ValidationReport report)
    {
        final Set<JsonNode> set = new HashSet<JsonNode>();

        for (final JsonNode element: schema.get("enum"))
            if (!set.add(element)) {
                report.message("elements in an enum array must be unique");
            }
    }
}
