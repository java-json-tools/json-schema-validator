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
import com.google.common.collect.ImmutableMap;
import org.eel.kitchen.jsonschema.ref.JsonPointer;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;

/**
 * Abstract validator for the {@code type} and {@code disallow} keywords
 *
 * <p>The structure of these two keywords are the same,
 * the only difference is the validation process.</p>
 */
public abstract class AbstractTypeKeywordValidator
    extends KeywordValidator
{
    /**
     * Shortcut for all JSON instance types
     */
    private static final String ANY = "any";

    /**
     * Simple types found in the keyword definition
     */
    protected final EnumSet<NodeType> typeSet = EnumSet.noneOf(NodeType.class);

    /**
     * Schemas found in the keyword definition
     */
    protected final Map<Integer, JsonNode> schemas;

    protected final JsonPointer basePtr;

    protected AbstractTypeKeywordValidator(final String keyword,
        final JsonNode schema)
    {
        super(keyword, NodeType.values());
        basePtr = JsonPointer.empty().append(keyword);

        final JsonNode node = schema.get(keyword);

        if (node.isTextual()) {
            addSimpleType(node.textValue());
            schemas = Collections.emptyMap();
            return;
        }

        final ImmutableMap.Builder<Integer, JsonNode> builder
            = new ImmutableMap.Builder<Integer, JsonNode>();

        JsonNode element;

        for (int i = 0; i < node.size(); i++) {
            element = node.get(i);
            if (element.isTextual())
                addSimpleType(element.textValue());
            else
                builder.put(i, element);
        }

        schemas = builder.build();
    }

    /**
     * Add a simple type to {@link #typeSet}
     *
     * <p>There are two special cases:</p>
     * <ul>
     *     <li>if type is {@link #ANY}, all values are filled in;</li>
     *     <li>if type is {@code number}, it also covers {@code integer}.</li>
     * </ul>
     *
     * @param type the type as a string
     */
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

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder(keyword)
            .append(": primitive types ");

        sb.append(typeSet.isEmpty() ? "(none)" : typeSet);

        if (!schemas.isEmpty())
            sb.append(", schemas: ").append(schemas.size());

        return sb.toString();
    }
}
