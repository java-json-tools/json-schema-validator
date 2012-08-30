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
import org.eel.kitchen.jsonschema.report.ValidationMessage;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.util.EnumSet;
import java.util.List;

/**
 * Dedicated syntax validator checker for keywords having arrays as arguments
 *
 * <p>This class checks that the element is an array, and that all elements
 * of this array are of a given set of types.
 * </p>
 */
public class ArrayChildrenSyntaxChecker
    extends SimpleSyntaxChecker
{
    private final EnumSet<NodeType> childrenTypes;

    protected ArrayChildrenSyntaxChecker(final String keyword,
        final EnumSet<NodeType> childrenTypes, final NodeType type,
        final NodeType... types)
    {
        super(keyword, type, types);
        this.childrenTypes = EnumSet.copyOf(childrenTypes);
    }

    @Override
    final void checkValue(final ValidationMessage.Builder msg,
        final List<ValidationMessage> messages, final JsonNode schema)
    {
        final JsonNode node = schema.get(keyword);

        if (!node.isArray())
            return;

        int index = 0;
        for (final JsonNode value: node) {
            final NodeType type = NodeType.getNodeType(value);
            if (!childrenTypes.contains(type)) {
                msg.setMessage("incorrect type for array element")
                    .addInfo("expected", childrenTypes)
                    .addInfo("found", type).addInfo("index", index);
                messages.add(msg.build());
            }
            index++;
        }
    }
}
