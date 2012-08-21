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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Syntax validator for the {@code enum} keyword
 */
public final class EnumSyntaxChecker
    extends SimpleSyntaxChecker
{
    private static final SyntaxChecker instance = new EnumSyntaxChecker();

    private EnumSyntaxChecker()
    {
        super("enum", NodeType.ARRAY);
    }

    public static SyntaxChecker getInstance()
    {
        return instance;
    }

    @Override
    void checkValue(final List<String> messages, final JsonNode schema)
    {
        final Set<JsonNode> values = new HashSet<JsonNode>();

        for (final JsonNode value: schema.get(keyword))
            if (!values.add(value)) {
                messages.add("values in an enum must be unique");
                return;
            }

        if (values.isEmpty())
            messages.add("an enum array must have at least one element");
    }
}
