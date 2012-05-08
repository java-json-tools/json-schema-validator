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

package org.eel.kitchen.jsonschema.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.util.NodeType;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractTypeKeywordValidator
    extends KeywordValidator
{
    private static final String ANY = "any";

    protected final EnumSet<NodeType> typeSet = EnumSet.noneOf(NodeType.class);
    protected final Set<JsonNode> schemas = new HashSet<JsonNode>();

    protected AbstractTypeKeywordValidator(final String keyword,
        final JsonNode schema)
    {
        super(NodeType.values());
        final JsonNode node = schema.get(keyword);

        if (node.isTextual()) {
            addSimpleType(node.textValue());
            return;
        }

        for (final JsonNode element: node)
            if (element.isTextual())
                addSimpleType(element.textValue());
            else
                schemas.add(element);
    }

    private void addSimpleType(final String type)
    {
        if (ANY.equals(type)) {
            typeSet.addAll(EnumSet.allOf(NodeType.class));
            return;
        }

        final NodeType tmp = NodeType.fromName(type);
        typeSet.add(tmp);
        if (tmp == NodeType.NUMBER)
            typeSet.add(NodeType.INTEGER);
    }
}
