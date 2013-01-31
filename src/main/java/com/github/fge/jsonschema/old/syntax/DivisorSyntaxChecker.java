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

package com.github.fge.jsonschema.old.syntax;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.report.Message;
import com.github.fge.jsonschema.util.NodeType;

import java.math.BigDecimal;
import java.util.List;

/**
 * Syntax checker for the {@code divisibleBy} keyword (draft v3) and {@code
 * multipleOf} keyword (draft v4)
 *
 * <p>This class ensures that the value of the keyword is a number which is
 * strictly greater than 0. Note that this include floating point numbers.</p>
 *
 */
public final class DivisorSyntaxChecker
    extends AbstractSyntaxChecker
{
    public DivisorSyntaxChecker(final String keyword)
    {
        super(keyword, NodeType.INTEGER, NodeType.NUMBER);
    }

    @Override
    public void checkValue(final SyntaxValidator validator,
        final List<Message> messages, final JsonNode schema)
    {
        final JsonNode node = schema.get(keyword);

        if (node.decimalValue().compareTo(BigDecimal.ZERO) > 0)
            return;

        final Message.Builder msg = newMsg().addInfo("value", node)
            .setMessage(keyword + " is not strictly greater than 0");

        messages.add(msg.build());
    }
}
