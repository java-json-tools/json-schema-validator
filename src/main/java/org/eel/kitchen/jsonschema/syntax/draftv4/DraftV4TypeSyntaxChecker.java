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
import com.google.common.collect.Sets;
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.syntax.AbstractSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Syntax validator for the (draft v4) {@code type} keyword
 */
public final class DraftV4TypeSyntaxChecker
    extends AbstractSyntaxChecker
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
        super("type", NodeType.ARRAY, NodeType.STRING);
    }

    @Override
    public void checkValue(final Message.Builder msg,
        final List<Message> messages, final JsonNode schema)
    {
        final JsonNode typeNode = schema.get(keyword);

        if (typeNode.isTextual()) {
            checkSimpleType(msg, messages, typeNode.textValue());
            return;
        }

        // If it is not a string it is an array: cycle through the elements,
        // and also check that the array is not empty

        final int size = typeNode.size();

        if (size == 0) {
            msg.setMessage("type array must not be empty");
            messages.add(msg.build());
            return;
        }

        final Set<JsonNode> set = Sets.newHashSet();

        JsonNode node;
        NodeType type;

        for (int index = 0; index < size; index++) {
            msg.addInfo("index", index);
            node = typeNode.get(index);
            type = NodeType.getNodeType(node);
            if (type != NodeType.STRING) {
                msg.setMessage("incorrect type for type array element")
                    .addInfo("found", type)
                    .addInfo("expected", NodeType.STRING);
                messages.add(msg.build());
            } else
                checkSimpleType(msg, messages, node.textValue());
            if (!set.add(node)) {
                msg.clearInfo()
                    .setMessage("elements in type array must be unique");
                messages.add(msg.build());
                return;
            }
        }
    }

    private static void checkSimpleType(final Message.Builder msg,
        final List<Message> messages, final String s)
    {
        if (!VALID_TYPES.contains(s)) {
            msg.setMessage("unknown simple type").addInfo("value", s)
                .addInfo("valid", VALID_TYPES);
            messages.add(msg.build());
        }
    }

}
