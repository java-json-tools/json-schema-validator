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
import org.eel.kitchen.jsonschema.main.ValidationMessage;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.util.EnumSet;
import java.util.List;

/**
 * Simple type-only syntax checker
 */
public class SimpleSyntaxChecker
    extends SyntaxChecker
{
    protected final String keyword;
    private final EnumSet<NodeType> validTypes;

    public SimpleSyntaxChecker(final String keyword, final NodeType type,
        final NodeType... types)
    {
        this.keyword = keyword;
        validTypes = EnumSet.of(type, types);
        msg.setKeyword(keyword);
    }

    @Override
    public final void checkSyntax(final List<ValidationMessage> messages,
        final JsonNode schema)
    {
        final NodeType nodeType = NodeType.getNodeType(schema.get(keyword));

        // Must be done: syntax checkers are uniquely instantiated!
        // FIXME: bug? But how to do better?
        msg.clearInfo();

        if (!validTypes.contains(nodeType)) {
            msg.addInfo("expected", validTypes).addInfo("found", nodeType)
                .setMessage("keyword is of wrong type");
            messages.add(msg.build());
            return;
        }

        checkValue(messages, schema);
    }

    void checkValue(final List<ValidationMessage> messages,
        final JsonNode schema)
    {
    }
}
