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
import com.google.common.collect.Sets;
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.syntax.AbstractSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;
import org.eel.kitchen.jsonschema.util.JacksonUtils;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Syntax validator for the (draft v4) {@code dependencies} keyword
 */
public final class DraftV4DependenciesSyntaxChecker
    extends AbstractSyntaxChecker
{
    private static final SyntaxChecker instance
        = new DraftV4DependenciesSyntaxChecker();

    public static SyntaxChecker getInstance()
    {
        return instance;
    }

    private DraftV4DependenciesSyntaxChecker()
    {
        super("dependencies", NodeType.OBJECT);
    }

    @Override
    public void checkValue(final Message.Builder msg,
        final List<Message> messages, final JsonNode schema)
    {
        final Map<String, JsonNode> map
            = JacksonUtils.nodeToMap(schema.get(keyword));

        JsonNode depValue;
        NodeType type;

        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            msg.addInfo("property", entry.getKey());
            depValue = entry.getValue();
            type = NodeType.getNodeType(depValue);
            switch (type) {
                case ARRAY:
                    checkPropertyDependency(msg, messages, depValue);
                    // Fall through
                case OBJECT:
                    break;
                default:
                    msg.setMessage("incorrect type for dependency value")
                        .addInfo("found", type)
                        .addInfo("expected",
                            EnumSet.of(NodeType.ARRAY, NodeType.OBJECT));
                    messages.add(msg.build());
            }
        }
    }

    private static void checkPropertyDependency(final Message.Builder msg,
        final List<Message> messages, final JsonNode depValue)
    {
        final int size = depValue.size();

        if (size == 0) {
            msg.setMessage("property dependency array must not be empty");
            messages.add(msg.build());
        }

        final Set<JsonNode> set = Sets.newHashSet();

        JsonNode node;
        NodeType type;

        for (int index = 0; index < size; index++) {
            node = depValue.get(index);
            type = NodeType.getNodeType(node);
            if (type != NodeType.STRING) {
                msg.setMessage("incorrect type for property dependency value")
                    .addInfo("index", index)
                    .addInfo("expected", NodeType.STRING)
                    .addInfo("found", type);
                messages.add(msg.build());
            }
            if (!set.add(node)) {
                msg.setMessage("elements in property array dependency must be "
                    + "unique");
                messages.add(msg.build());
                return;
            }
        }
    }
}
