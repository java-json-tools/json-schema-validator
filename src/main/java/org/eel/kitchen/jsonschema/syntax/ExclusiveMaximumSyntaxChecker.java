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

import java.util.List;

/**
 * Syntax validator for the {@code exclusiveMaximum} keyword
 */
public final class ExclusiveMaximumSyntaxChecker
    extends SimpleSyntaxChecker
{
    private static final SyntaxChecker instance
        = new ExclusiveMaximumSyntaxChecker();

    public static SyntaxChecker getInstance()
    {
        return instance;
    }

    private ExclusiveMaximumSyntaxChecker()
    {
        super("exclusiveMaximum", NodeType.BOOLEAN);
    }

    @Override
    void checkValue(final ValidationMessage.Builder msg,
        final List<ValidationMessage> messages, final JsonNode schema)
    {
        if (schema.has("maximum"))
            return;

        messages.add(msg.setMessage(keyword + " must be paired with maximum")
            .build());
    }
}
