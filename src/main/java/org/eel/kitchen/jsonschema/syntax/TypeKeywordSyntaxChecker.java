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
import org.eel.kitchen.jsonschema.util.NodeType;

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
    final void checkValue(final List<String> messages, final JsonNode schema)
    {
        final JsonNode node = schema.get(keyword);

        if (!node.isArray()) {
            validateOne(messages, node);
            return;
        }

        final Set<JsonNode> set = new HashSet<JsonNode>();

        for (final JsonNode value: node) {
            if (!set.add(value)) {
                messages.add("items in the array must be unique");
                return;
            }
            validateOne(messages, value);
        }
    }

    private static void validateOne(final List<String> messages,
        final JsonNode value)
    {
        if (value.isObject())
            return;

        if (!value.isTextual()) {
            messages.add("value has wrong type");
            return;
        }

        final String s = value.textValue();

        if (ANY.equals(s))
            return;

        if (NodeType.fromName(s) == null)
            messages.add("unknown simple type");
    }
}
