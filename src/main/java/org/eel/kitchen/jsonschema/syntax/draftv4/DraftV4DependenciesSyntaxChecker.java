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
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.syntax.SimpleSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;
import org.eel.kitchen.jsonschema.util.JacksonUtils;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.util.List;
import java.util.Map;

public final class DraftV4DependenciesSyntaxChecker
    extends SimpleSyntaxChecker
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

        NodeType type;

        msg.setMessage("incorrect type for dependency value");

        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            type = NodeType.getNodeType(entry.getValue());
            if (type == NodeType.OBJECT)
                continue;
            msg.addInfo("property", entry.getKey()).addInfo("found", type)
                .addInfo("expected", NodeType.OBJECT);
            messages.add(msg.build());
        }
    }
}
