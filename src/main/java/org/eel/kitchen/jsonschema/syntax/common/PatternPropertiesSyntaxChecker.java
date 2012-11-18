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

package org.eel.kitchen.jsonschema.syntax.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.syntax.AbstractSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.util.RhinoHelper;

import java.util.List;
import java.util.Set;

/**
 * Syntax validator for the {@code patternProperties} keyword
 *
 * <p>This syntax checker does more than the metaschema itself, since it also
 * checks that member names are valid regular expressions.</p>
 *
 * @see RhinoHelper
 */
public final class PatternPropertiesSyntaxChecker
    extends AbstractSyntaxChecker
{
    private static final SyntaxChecker instance
        = new PatternPropertiesSyntaxChecker();

    private PatternPropertiesSyntaxChecker()
    {
        super("patternProperties", NodeType.OBJECT);
    }

    public static SyntaxChecker getInstance()
    {
        return instance;
    }

    @Override
    public void checkValue(final Message.Builder msg,
        final List<Message> messages, final JsonNode schema)
    {
        final JsonNode node = schema.get(keyword);
        final Set<String> regexes = Sets.newHashSet(node.fieldNames());

        NodeType type;

        for (final String regex: Ordering.natural().sortedCopy(regexes)) {
            msg.clearInfo().addInfo("key", regex);
            if (!RhinoHelper.regexIsValid(regex)) {
                msg.setMessage("key is not a valid ECMA 262 regex");
                messages.add(msg.build());
                // No need to continue: even if we were to continue and check
                // the value, the latter would never be picked up anyway.
                continue;
            }
            type = NodeType.getNodeType(node.get(regex));
            if (type == NodeType.OBJECT)
                continue;
            msg.setMessage("illegal key value").addInfo("found", type)
                .addInfo("expected", NodeType.OBJECT);
            messages.add(msg.build());
        }
    }
}
