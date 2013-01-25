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

package com.github.fge.jsonschema.syntax.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.report.Message;
import com.github.fge.jsonschema.syntax.AbstractSyntaxChecker;
import com.github.fge.jsonschema.syntax.SyntaxChecker;
import com.github.fge.jsonschema.syntax.SyntaxValidator;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.util.RhinoHelper;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

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
    private static final SyntaxChecker INSTANCE
        = new PatternPropertiesSyntaxChecker();

    private PatternPropertiesSyntaxChecker()
    {
        super("patternProperties", NodeType.OBJECT);
    }

    public static SyntaxChecker getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void checkValue(final SyntaxValidator validator,
        final List<Message> messages, final JsonNode schema)
    {
        final JsonNode node = schema.get(keyword);
        final Set<String> regexes = Sets.newHashSet(node.fieldNames());

        NodeType type;
        JsonNode subSchema;
        Message.Builder msg;

        for (final String regex: Ordering.natural().sortedCopy(regexes)) {
            msg = newMsg().addInfo("key", regex);
            if (!RhinoHelper.regexIsValid(regex)) {
                msg.setMessage("key is not a valid ECMA 262 regex");
                messages.add(msg.build());
                // No need to continue: even if we were to continue and check
                // the value, the latter would never be picked up anyway.
                continue;
            }
            subSchema = node.get(regex);
            type = NodeType.getNodeType(subSchema);
            if (type != NodeType.OBJECT) {
                msg.setMessage("illegal key value").addInfo("found", type)
                    .addInfo("expected", NodeType.OBJECT);
                messages.add(msg.build());
                continue;
            }
            validator.validate(messages, subSchema);
        }
    }
}
