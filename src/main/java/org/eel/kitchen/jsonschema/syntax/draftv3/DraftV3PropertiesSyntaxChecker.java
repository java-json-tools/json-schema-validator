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
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.syntax.AbstractSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.util.List;
import java.util.Set;

/**
 * Syntax validator for the {@code properties} keyword
 *
 * <p>In fact, what this validator really does is check that a {@code required}
 * member in a value, if any, is a boolean.</p>
 */
public final class DraftV3PropertiesSyntaxChecker
    extends AbstractSyntaxChecker
{
    private static final SyntaxChecker INSTANCE
        = new DraftV3PropertiesSyntaxChecker();

    private DraftV3PropertiesSyntaxChecker()
    {
        super("properties", NodeType.OBJECT);
    }

    public static SyntaxChecker getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void checkValue(final SyntaxValidator validator,
        final Message.Builder msg, final List<Message> messages,
        final JsonNode schema)
    {
        final JsonNode node = schema.get(keyword);
        final Set<String> fields = Sets.newHashSet(node.fieldNames());

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
                    .addInfo("expected", NodeType.OBJECT)
                    .addInfo("found", type);
                messages.add(msg.build());
                continue;
            }
            /*
             * If the subschema has a "required" member name, check that the
             * value is a boolean
             */
            if (element.has("required")) {
                type = NodeType.getNodeType(element.get("required"));
                if (type != NodeType.BOOLEAN) {
                    msg.setMessage("\"required\" attribute has incorrect type")
                        .addInfo("expected", NodeType.BOOLEAN)
                        .addInfo("found", type);
                    messages.add(msg.build());
                    continue;
                }
            }
            validator.validate(messages, element);
        }
    }
}
