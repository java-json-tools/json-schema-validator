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
import org.eel.kitchen.jsonschema.report.Domain;
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.util.EnumSet;
import java.util.List;

/**
 * Basic syntax checker
 *
 * <p>All other syntax checkers inherit from this class. Its only purpose is to
 * check whether the type of the keyword's value is of the expected type(s).
 * More advanced syntax checkers will override {@link
 * #checkValue(SyntaxValidator, List, JsonNode)} to further check the anatomy of
 * this value.</p>
 */
public abstract class AbstractSyntaxChecker
    implements SyntaxChecker
{
    protected final String keyword;
    private final EnumSet<NodeType> validTypes;

    protected AbstractSyntaxChecker(final String keyword, final NodeType type,
        final NodeType... types)
    {
        this.keyword = keyword;
        validTypes = EnumSet.of(type, types);
    }

    @Override
    public final void checkSyntax(final SyntaxValidator validator,
        final List<Message> messages, final JsonNode schema)
    {
        final NodeType nodeType = NodeType.getNodeType(schema.get(keyword));

        if (!validTypes.contains(nodeType)) {
            messages.add(newMsg().setMessage("incorrect type for value")
                .addInfo("expected", validTypes).addInfo("found", nodeType)
                .build());
            return;
        }

        checkValue(validator, messages, schema);
    }

    public abstract void checkValue(final SyntaxValidator validator,
        final List<Message> messages, final JsonNode schema);

    protected final Message.Builder newMsg()
    {
        return Domain.SYNTAX.newMessage().setKeyword(keyword);
    }
}
