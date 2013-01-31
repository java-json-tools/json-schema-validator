/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
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

import java.util.List;

/**
 * Syntax validator common for {@code items} (draft v3 and v4) and {@code
 * extends} (draft v3)
 */
public final class SchemaOrSchemaArraySyntaxChecker
    extends AbstractSyntaxChecker
{
    private final boolean allowEmptyArrays;

    public SchemaOrSchemaArraySyntaxChecker(final String keyword,
        final boolean allowEmptyArrays)
    {
        super(keyword, NodeType.ARRAY, NodeType.OBJECT);
        this.allowEmptyArrays = allowEmptyArrays;
    }

    @Override
    public void checkValue(final SyntaxValidator validator,
        final List<Message> messages, final JsonNode schema)
    {
        final JsonNode node = schema.get(keyword);
        final Message.Builder msg = newMsg();

        if (!node.isArray()) {
            validator.validate(messages, node);
            return;
        }

        /*
         * If it is an array:
         *
         * - if we do not allow empty arrays, check that the array is not empty;
         * - check that each item of this array is a schema
         */

        final int size = node.size();

        if (size == 0 && !allowEmptyArrays) {
            msg.setMessage("array must have at least one element");
            messages.add(msg.build());
            return;
        }

        JsonNode subSchema;
        NodeType type;

        for (int index = 0; index < size; index++) {
            subSchema = node.get(index);
            type = NodeType.getNodeType(node.get(index));
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
