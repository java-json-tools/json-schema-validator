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
import org.codehaus.jackson.node.ObjectNode;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;

public final class TypeSchemaExpander
    implements Iterator<JsonNode>
{
    private static final JsonNodeFactory factory = JsonNodeFactory.instance;

    private static final JsonNode ANY = factory.textNode("any");

    private static final Set<JsonNode> ALL_SIMPLE_TYPES;

    static {
        ALL_SIMPLE_TYPES = new HashSet<JsonNode>();
        for (final NodeType type: EnumSet.allOf(NodeType.class))
            ALL_SIMPLE_TYPES.add(factory.textNode(type.toString()));
    }

    private final Queue<JsonNode> typeNodes = new ArrayDeque<JsonNode>();

    private final ObjectNode fields = factory.objectNode();

    private Iterator<JsonNode> next = null;

    public TypeSchemaExpander(final JsonNode schema)
    {
        fields.putAll(CollectionUtils.toMap(schema.getFields()));

        final JsonNode typeNode = fields.remove("type");

        if (typeNode == null) {
            typeNodes.addAll(ALL_SIMPLE_TYPES);
            return;
        }

        final Collection<JsonNode> elements = new HashSet<JsonNode>();

        if (typeNode.isTextual()) {
            if (!ANY.equals(typeNode))
                NodeType.valueOf(typeNode.getTextValue().toUpperCase());
            elements.add(typeNode);
        } else if (typeNode.isArray())
            elements.addAll(CollectionUtils.toSet(typeNode.getElements()));
        else
            throw new IllegalArgumentException("illegal type for type node");

        if (elements.remove(ANY))
            elements.addAll(ALL_SIMPLE_TYPES);

        typeNodes.addAll(elements);
    }

    @Override
    public boolean hasNext()
    {
        return !typeNodes.isEmpty() || next != null;
    }

    @Override
    public JsonNode next()
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
            final ObjectNode ret = factory.objectNode();
            ret.putAll(fields);
            ret.put("type", node.getTextValue());
            return ret;
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
