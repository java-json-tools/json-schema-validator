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

package org.eel.kitchen.jsonschema.syntax;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.ValidationMessage;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Dedicated syntax checker for {@code type} and {@code disallow}
 *
 * <p>These keywords are monsters. Only {@code dependencies} comes close in
 * terms of complexity.</p>
 */
public class TypeKeywordSyntaxChecker
    extends SimpleSyntaxChecker
{
    private static final String ANY = "any";

    public TypeKeywordSyntaxChecker(final String keyword)
    {
        super(keyword, NodeType.STRING, NodeType.ARRAY);
    }

    @Override
    final void checkValue(final List<ValidationMessage> messages,
        final JsonNode schema)
    {
        final JsonNode node = schema.get(keyword);

        if (!node.isArray()) {
            validateOne(messages, node);
            return;
        }

        final Set<JsonNode> set = new HashSet<JsonNode>();

        for (final JsonNode value: node) {
            if (!set.add(value)) {
                msg.setMessage("duplicate value found in array (the spec " +
                    "forbids that)");
                messages.add(msg.build());
                return;
            }
            validateOne(messages, value);
        }
    }

    private void validateOne(final List<ValidationMessage> messages,
        final JsonNode value)
    {
        // Cannot happen in the event of single property validation (will
        // always be a string)
        if (value.isObject())
            return;

        // Clear information here, other undesired error information may leak
        // otherwise (?)
        msg.clearInfo();

        // See above
        if (!value.isTextual()) {
            msg.addInfo("elementType", NodeType.getNodeType(value))
                .setMessage("array element is neither a primitive type nor " +
                "a schema (neither a string nor an object)");
            messages.add(msg.build());
            return;
        }

        // Now we can actually check that the string is a valid primitive type
        final String s = value.textValue();

        if (ANY.equals(s))
            return;

        if (NodeType.fromName(s) != null)
            return;

        msg.addInfo("validValues", EnumSet.allOf(NodeType.class))
            .addInfo("found", s).setMessage("unknow simple type");
        messages.add(msg.build());
    }
}
