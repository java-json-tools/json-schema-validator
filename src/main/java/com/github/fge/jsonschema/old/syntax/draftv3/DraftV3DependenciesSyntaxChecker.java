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

package com.github.fge.jsonschema.old.syntax.draftv3;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.old.syntax.AbstractSyntaxChecker;
import com.github.fge.jsonschema.old.syntax.SyntaxChecker;
import com.github.fge.jsonschema.old.syntax.SyntaxValidator;
import com.github.fge.jsonschema.report.Message;
import com.github.fge.jsonschema.util.NodeType;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Syntax checker for the (draft v3) {@code dependencies} keyword
 */
public final class DraftV3DependenciesSyntaxChecker
    extends AbstractSyntaxChecker
{
    private static final EnumSet<NodeType> VALID_DEPENDENCY_TYPES
        = EnumSet.of(NodeType.OBJECT, NodeType.ARRAY, NodeType.STRING);

    private static final SyntaxChecker INSTANCE
        = new DraftV3DependenciesSyntaxChecker();

    private DraftV3DependenciesSyntaxChecker()
    {
        super("dependencies", NodeType.OBJECT);
    }

    public static SyntaxChecker getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void checkValue(final SyntaxValidator validator,
        final List<Message> messages, final JsonNode schema)
    {
        /*
         * At that point, we know this is an array. Build a map out of it and
         * call an internal validation method on each map entry -- see below.
         *
         * For convenience reasons, we also use a SortedMap so that messages
         * appear in property natural order: while there is no defined order in
         * a JSON Object, chances are very high that the schema will be written
         * with properties in order, so we might as well not confuse the user.
         */
        final JsonNode node = schema.get(keyword);
        final Set<String> fields = Sets.newHashSet(node.fieldNames());

        JsonNode element;
        NodeType type;
        Message.Builder msg;
        int size;

        for (final String field: Ordering.natural().sortedCopy(fields)) {
            msg = newMsg().addInfo("property", field);
            element = node.get(field);
            type = NodeType.getNodeType(element);
            switch (type) {
                case OBJECT:
                    validator.validate(messages, element);
                    // Fall through
                case STRING:
                    continue;
                case ARRAY:
                    break;
                default:
                    msg.addInfo("found", type)
                        .addInfo("expected", VALID_DEPENDENCY_TYPES)
                        .setMessage("dependency value has incorrect type");
                    messages.add(msg.build());
                    continue;
            }
            size = element.size();
            for (int index = 0; index < size; index++) {
                type = NodeType.getNodeType(element.get(index));
                if (type == NodeType.STRING)
                    continue;
                msg.addInfo("index", index).addInfo("found", type)
                    .addInfo("expected", NodeType.STRING)
                    .setMessage("array dependency value has incorrect type");
                messages.add(msg.build());

            }
        }
    }
}
