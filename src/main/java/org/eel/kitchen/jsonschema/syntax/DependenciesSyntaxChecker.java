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
import org.eel.kitchen.jsonschema.util.NodeType;

import java.util.List;

/**
 * Syntax checker for the {@code dependencies} keyword
 */
public final class DependenciesSyntaxChecker
    extends SimpleSyntaxChecker
{
    private static final SyntaxChecker instance
        = new DependenciesSyntaxChecker();

    private DependenciesSyntaxChecker()
    {
        super("dependencies", NodeType.OBJECT);
    }

    public static SyntaxChecker getInstance()
    {
        return instance;
    }

    @Override
    void checkValue(final List<String> messages, final JsonNode schema)
    {
        for (final JsonNode value: schema.get(keyword)) {
            switch (NodeType.getNodeType(value)) {
                case ARRAY:
                    for (final JsonNode element : value)
                        if (!element.isTextual())
                            messages.add("array element is not a string");
                    // Fall through
                case OBJECT:
                case STRING:
                    break;
                default:
                    messages.add("dependencies element has wrong type");
            }
        }
    }
}
