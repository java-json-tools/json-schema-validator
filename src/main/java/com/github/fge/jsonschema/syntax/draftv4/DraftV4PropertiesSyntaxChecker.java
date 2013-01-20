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

package com.github.fge.jsonschema.syntax.draftv4;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.github.fge.jsonschema.report.Message;
import com.github.fge.jsonschema.syntax.AbstractSyntaxChecker;
import com.github.fge.jsonschema.syntax.SyntaxChecker;
import com.github.fge.jsonschema.syntax.SyntaxValidator;
import com.github.fge.jsonschema.util.NodeType;

import java.util.List;
import java.util.Set;

/**
 * Syntax validator for the {@code properties} keyword
 */
public final class DraftV4PropertiesSyntaxChecker
    extends AbstractSyntaxChecker
{
    private static final SyntaxChecker INSTANCE
        = new DraftV4PropertiesSyntaxChecker();

    private DraftV4PropertiesSyntaxChecker()
    {
        super("properties", NodeType.OBJECT);
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
        final Set<String> fields = Sets.newHashSet(node.fieldNames());
        final Message.Builder msg = newMsg();

        NodeType type;
        JsonNode element;

        for (final String field: Ordering.natural().sortedCopy(fields)) {
            msg.addInfo("key", field);
            element = node.get(field);
            /*
             * Check that member values are JSON objects (schemas)
             */
            type = NodeType.getNodeType(element);
            if (type != NodeType.OBJECT) {
                msg.setMessage("key value has incorrect type")
                    .addInfo("expected", NodeType.OBJECT).addInfo("found", type);
                messages.add(msg.build());
                continue;
            }
            validator.validate(messages, element);
        }
    }
}
