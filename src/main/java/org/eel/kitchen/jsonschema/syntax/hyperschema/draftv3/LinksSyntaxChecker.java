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

package org.eel.kitchen.jsonschema.syntax.hyperschema.draftv3;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.syntax.AbstractSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

public final class LinksSyntaxChecker
    extends AbstractSyntaxChecker
{
    private static final Set<String> LDO_REQUIRED_MEMBERS
        = ImmutableSet.of("href", "rel");

    private static final SyntaxChecker INSTANCE = new LinksSyntaxChecker();

    private LinksSyntaxChecker()
    {
        super("links", NodeType.ARRAY);
    }

    public static SyntaxChecker getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void checkValue(final Message.Builder msg,
        final List<Message> messages, final JsonNode schema)
    {
        final JsonNode value = schema.get(keyword);
        final int size = value.size();

        JsonNode node;
        NodeType type;

        for (int index = 0; index < size; index++) {
            node = value.get(index);
            type = NodeType.getNodeType(node);
            msg.addInfo("index", index);
            if (type != NodeType.OBJECT) {
                msg.addInfo("expected", NodeType.OBJECT).addInfo("found", type)
                    .setMessage("incorrect array element type");
                messages.add(msg.build());
                continue;
            }
            checkLDOSyntax(msg, messages, node);
        }
    }

    private void checkLDOSyntax(final Message.Builder msg,
        final List<Message> messages, final JsonNode node)
    {
        final Set<String> memberNames = Sets.newHashSet(node.fieldNames());

        if (memberNames.containsAll(LDO_REQUIRED_MEMBERS))
            return;

        final SortedSet<String> missing = Sets.newTreeSet(LDO_REQUIRED_MEMBERS);

        missing.removeAll(memberNames);

        msg.addInfo("required", LDO_REQUIRED_MEMBERS)
            .addInfo("missing", missing)
            .setMessage("missing required properties in link description "
                + "object");
        messages.add(msg.build());
    }
}
