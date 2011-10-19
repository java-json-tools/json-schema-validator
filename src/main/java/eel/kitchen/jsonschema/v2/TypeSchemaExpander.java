/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.v2;

import eel.kitchen.util.CollectionUtils;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;

import java.util.ArrayDeque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;

public final class TypeSchemaExpander
    implements Iterator<JsonSubSchema>
{
    private static final JsonNodeFactory factory = JsonNodeFactory.instance;

    private final Queue<JsonNode> typeNodes = new ArrayDeque<JsonNode>();

    private final JsonNode subSchema;

    private Iterator<JsonSubSchema> next = null;

    public TypeSchemaExpander(final JsonNode schema)
    {
        final Map<String, JsonNode> fields = new HashMap<String, JsonNode>();

        fields.putAll(CollectionUtils.toMap(schema.getFields()));

        final JsonNode typeNode = fields.remove("type");

        subSchema = factory.objectNode().putAll(fields);

        if (typeNode == null) {
            for (final NodeType type : EnumSet.allOf(NodeType.class))
                typeNodes.add(factory.textNode(type.toString()));
            return;
        }

        if (typeNode.isTextual()) {
            typeNodes.add(typeNode);
            return;
        }

        typeNodes.addAll(CollectionUtils.toSet(typeNode.getElements()));
    }

    @Override
    public boolean hasNext()
    {
        return !typeNodes.isEmpty() || next != null;
    }

    @Override
    public JsonSubSchema next()
    {
        if (!hasNext())
            throw new NoSuchElementException();

        if (next != null) {
            if (next.hasNext())
                return next.next();
            next = null;
        }

        final JsonNode node = typeNodes.remove();

        if (node.isTextual()) {
            final String typeName = node.getTextValue().toUpperCase();
            return new JsonSubSchema(NodeType.valueOf(typeName), subSchema);
        }

        next = new TypeSchemaExpander(node);

        return next.next();
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
