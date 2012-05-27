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
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.util.NodeType;

import java.util.EnumSet;

public abstract class ArrayChildrenSyntaxChecker
    extends SyntaxChecker
{
    private final EnumSet<NodeType> childrenTypes;

    protected ArrayChildrenSyntaxChecker(final String keyword,
        final EnumSet<NodeType> childrenTypes)
    {
        super(keyword);
        this.childrenTypes = EnumSet.copyOf(childrenTypes);
    }

    @Override
    final void checkValue(final ValidationReport report,
        final JsonNode schema)
    {
        final JsonNode node = schema.get(keyword);

        if (!node.isArray())
            return;

        for (final JsonNode value: node)
            if (!childrenTypes.contains(NodeType.getNodeType(value)))
                report.addMessage("wrong element type in array");
    }
}
