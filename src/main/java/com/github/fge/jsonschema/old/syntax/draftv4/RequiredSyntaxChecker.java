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

package com.github.fge.jsonschema.old.syntax.draftv4;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.report.Message;
import com.github.fge.jsonschema.old.syntax.AbstractSyntaxChecker;
import com.github.fge.jsonschema.old.syntax.SyntaxChecker;
import com.github.fge.jsonschema.old.syntax.SyntaxValidator;
import com.github.fge.jsonschema.util.NodeType;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

/**
 * Syntax validator for the (draft v4) {@code required} keyword
 */
public final class RequiredSyntaxChecker
    extends AbstractSyntaxChecker
{
    private static final SyntaxChecker INSTANCE = new RequiredSyntaxChecker();

    public static SyntaxChecker getInstance()
    {
        return INSTANCE;
    }

    private RequiredSyntaxChecker()
    {
        super("required", NodeType.ARRAY);
    }

    @Override
    public void checkValue(final SyntaxValidator validator,
        final List<Message> messages, final JsonNode schema)
    {
        final JsonNode node = schema.get(keyword);
        final Message.Builder msg = newMsg();

        final int size = node.size();

        if (size == 0) {
            msg.setMessage("array must have at least one element");
            messages.add(msg.build());
            return;
        }

        final Set<String> set = Sets.newHashSet();
        NodeType type;
        JsonNode element;

        for (int index = 0; index < size; index++) {
            element = node.get(index);
            type = NodeType.getNodeType(element);
            if (type != NodeType.STRING) {
                msg.clearInfo().setMessage("incorrect type for array element")
                    .addInfo("index", index).addInfo("found", type)
                    .addInfo("expected", NodeType.STRING);
                messages.add(msg.build());
                continue;
            }
            if (!set.add(element.textValue())) {
                msg.setMessage("elements in the array must be unique");
                messages.add(msg.clearInfo().build());
                return;
            }
        }
    }
}
