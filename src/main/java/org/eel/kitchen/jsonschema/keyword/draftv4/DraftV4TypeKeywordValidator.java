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

package org.eel.kitchen.jsonschema.keyword.draftv4;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.validator.ValidationContext;

import java.util.EnumSet;

/**
 * Keyword validator for the (draft v4) {@code type} keyword
 *
 * <p>Unlike draft v3, this keyword's value can now only be a single string,
 * which is one of the seven types defined by the draft.</p>
 */
public final class DraftV4TypeKeywordValidator
    extends KeywordValidator
{
    private final EnumSet<NodeType> expected = EnumSet.noneOf(NodeType.class);

    public DraftV4TypeKeywordValidator(final JsonNode schema)
    {
        super("type", NodeType.values());

        final JsonNode typeNode = schema.get(keyword);

        if (typeNode.isTextual()) {
            addSimpleType(typeNode);
            return;
        }

        // Array of simple types

        for (final JsonNode node: typeNode)
            addSimpleType(node);
    }

    @Override
    protected void validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        final NodeType type = NodeType.getNodeType(instance);

        if (expected.contains(type))
            return;

        final Message msg = newMsg().setMessage("instance has incorrect type")
            .addInfo("found", type).addInfo("expected", expected).build();
        report.addMessage(msg);
    }

    @Override
    public String toString()
    {
        return "type: " + expected;
    }

    private void addSimpleType(final JsonNode node)
    {
        final NodeType type = NodeType.fromName(node.textValue());
        expected.add(type);

        if (type == NodeType.NUMBER)
            expected.add(NodeType.INTEGER);
    }
}
