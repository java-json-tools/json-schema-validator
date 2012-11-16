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

package org.eel.kitchen.jsonschema.syntax.draftv4;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Functions;
import com.google.common.collect.FluentIterable;
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.syntax.SimpleSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Syntax validator for the (draft v4) {@code type} keyword
 */
public final class DraftV4TypeSyntaxChecker
    extends SimpleSyntaxChecker
{
    private static final SyntaxChecker instance
        = new DraftV4TypeSyntaxChecker();

    private static final Set<String> VALID_TYPES
        = FluentIterable.from(EnumSet.allOf(NodeType.class))
            .transform(Functions.toStringFunction()).toImmutableSet();

    public static SyntaxChecker getInstance()
    {
        return instance;
    }

    private DraftV4TypeSyntaxChecker()
    {
        super("type", NodeType.STRING);
    }

    @Override
    public void checkValue(final Message.Builder msg,
        final List<Message> messages, final JsonNode schema)
    {
        final String value = schema.get(keyword).textValue();

        if (VALID_TYPES.contains(value))
            return;

        msg.setMessage("unknown simple type").addInfo("value", value)
            .addInfo("valid", VALID_TYPES);

        messages.add(msg.build());
    }
}
