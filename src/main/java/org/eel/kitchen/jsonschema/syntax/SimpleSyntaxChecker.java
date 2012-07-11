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

import java.util.EnumSet;
import java.util.List;

/**
 * Simple type-only syntax checker
 */
public class SimpleSyntaxChecker
    implements SyntaxChecker
{
    protected final String keyword;
    private final EnumSet<NodeType> validTypes;

    public SimpleSyntaxChecker(final String keyword, final NodeType type,
        final NodeType... types)
    {
        this.keyword = keyword;
        validTypes = EnumSet.of(type, types);
    }

    @Override
    public final void checkSyntax(final List<String> messages,
        final JsonNode schema)
    {
        final NodeType nodeType = NodeType.getNodeType(schema.get(keyword));
        if (!validTypes.contains(nodeType)) {
            messages.add("keyword is of wrong type");
            return;
        }

        checkValue(messages, schema);
    }

    void checkValue(final List<String> messages, final JsonNode schema)
    {
    }
}
