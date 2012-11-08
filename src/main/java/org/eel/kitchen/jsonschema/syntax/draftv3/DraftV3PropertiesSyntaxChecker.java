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

package org.eel.kitchen.jsonschema.syntax.draftv3;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.syntax.SimpleSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;
import org.eel.kitchen.jsonschema.util.JacksonUtils;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * Syntax validator for the {@code properties} keyword
 */
public final class DraftV3PropertiesSyntaxChecker
    extends SimpleSyntaxChecker
{
    private static final SyntaxChecker instance
        = new DraftV3PropertiesSyntaxChecker();

    private DraftV3PropertiesSyntaxChecker()
    {
        super("properties", NodeType.OBJECT);
    }

    public static SyntaxChecker getInstance()
    {
        return instance;
    }

    @Override
    public void checkValue(final Message.Builder msg,
        final List<Message> messages, final JsonNode schema)
    {
        final SortedMap<String, JsonNode> map
            = JacksonUtils.nodeToTreeMap(schema.get(keyword));

        NodeType type;
        JsonNode value;

        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            msg.addInfo("key", entry.getKey());
            value = entry.getValue();
            type = NodeType.getNodeType(entry.getValue());
            if (!value.isObject()) {
                msg.setMessage("key value has incorrect type")
                    .addInfo("expected", NodeType.OBJECT)
                    .addInfo("found", type);
                messages.add(msg.build());
                continue;
            }
            if (!value.has("required"))
                continue;
            type = NodeType.getNodeType(value.get("required"));
            if (type == NodeType.BOOLEAN)
                continue;
            msg.setMessage("\"required\" attribute has incorrect type")
                .addInfo("expected", NodeType.BOOLEAN)
                .addInfo("found", type);
            messages.add(msg.build());
        }
    }
}
