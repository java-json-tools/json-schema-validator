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

package org.eel.kitchen.jsonschema.syntax;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.util.NodeType;

@ValidTypes(NodeType.OBJECT)
public final class DependenciesSyntaxChecker
    extends SyntaxChecker
{
    private static final SyntaxChecker instance
        = new DependenciesSyntaxChecker();

    private DependenciesSyntaxChecker()
    {
        super("dependencies");
    }

    public static SyntaxChecker getInstance()
    {
        return instance;
    }

    @Override
    void checkValue(final ValidationReport report,
        final JsonNode schema)
    {
        for (final JsonNode value: schema.get(keyword)) {
            switch (NodeType.getNodeType(value)) {
                case ARRAY:
                    for (final JsonNode element : value)
                        if (!element.isTextual())
                            report.addMessage("array element is not a string");
                    // Fall through
                case OBJECT:
                case STRING:
                    break;
                default:
                    report.addMessage("dependencies element has wrong type");
            }
        }
    }
}
