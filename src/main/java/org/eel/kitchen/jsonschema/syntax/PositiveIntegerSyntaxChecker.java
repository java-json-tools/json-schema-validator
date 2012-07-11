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
import org.eel.kitchen.util.NodeType;

import java.util.List;

public class PositiveIntegerSyntaxChecker
    extends SimpleSyntaxChecker
{
    public PositiveIntegerSyntaxChecker(final String keyword)
    {
        super(keyword, NodeType.INTEGER);
    }

    @Override
    final void checkValue(final List<String> messages, final JsonNode schema)
    {
        final JsonNode node = schema.get(keyword);

        if (!node.canConvertToInt()) {
            messages.add("value overflow");
            return;
        }

        if (node.intValue() < 0)
            messages.add("value is negative");
    }
}
