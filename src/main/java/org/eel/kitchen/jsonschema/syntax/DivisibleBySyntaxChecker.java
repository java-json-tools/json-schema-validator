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
import org.eel.kitchen.jsonschema.report.ValidationMessage;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.math.BigDecimal;
import java.util.List;

/**
 * Syntax checker for the {@code divisibleBy} keyword
 *
 * <p>Note: in draft v4, this will be renamed to {@code mod}.</p>
 *
 */
public final class DivisibleBySyntaxChecker
    extends SimpleSyntaxChecker
{
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private static final SyntaxChecker instance
        = new DivisibleBySyntaxChecker();

    private DivisibleBySyntaxChecker()
    {
        super("divisibleBy", NodeType.INTEGER, NodeType.NUMBER);
    }

    public static SyntaxChecker getInstance()
    {
        return instance;
    }

    @Override
    void checkValue(final ValidationMessage.Builder msg,
        final List<ValidationMessage> messages, final JsonNode schema)
    {
        final JsonNode node = schema.get(keyword);

        if (node.decimalValue().compareTo(ZERO) > 0)
            return;

        msg.setMessage("divisibleBy is not strictly greater than 0")
            .addInfo("value", node);

        messages.add(msg.build());
    }
}
