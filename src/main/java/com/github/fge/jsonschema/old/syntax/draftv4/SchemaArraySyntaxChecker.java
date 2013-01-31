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
import com.github.fge.jsonschema.old.syntax.SyntaxValidator;
import com.github.fge.jsonschema.util.NodeType;

import java.util.List;

/**
 * Common syntax validator for keywords having a schema array as an argument
 *
 * <p>These keywords are: {@code anyOf}, {@code allOf} and {@code oneOf}.</p>
 */
public final class SchemaArraySyntaxChecker
    extends AbstractSyntaxChecker
{
    public SchemaArraySyntaxChecker(final String keyword)
    {
        super(keyword, NodeType.ARRAY);
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

        JsonNode subSchema;
        NodeType type;

        for (int index = 0; index < size; index++) {
            subSchema = node.get(index);
            type = NodeType.getNodeType(subSchema);
            if (type != NodeType.OBJECT) {
                msg.setMessage("incorrect type for array element")
                    .addInfo("index", index).addInfo("found", type)
                    .addInfo("expected", NodeType.OBJECT);
                messages.add(msg.build());
                continue;
            }
            validator.validate(messages, subSchema);
        }
    }
}
