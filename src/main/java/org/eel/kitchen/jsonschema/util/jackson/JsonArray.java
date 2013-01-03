/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
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

package org.eel.kitchen.jsonschema.util.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;

/**
 * Override of Jackson's {@link ArrayNode}
 *
 * <p>Unlike in the original, here the list of elements is never {@code null}.
 * This allows for much simpler code. Also, the class is final.</p>
 */
public final class JsonArray
    extends ArrayNode
{
    private static final Joiner JOINER = Joiner.on(',');

    public JsonArray(final JsonNodeFactory nc)
    {
        super(nc);
        _children = Lists.newArrayList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public JsonArray deepCopy()
    {
        final JsonArray ret = new JsonArray(_nodeFactory);
        for (final JsonNode element: _children)
            ret._children.add(element.deepCopy());
        return ret;
    }

    @Override
    public int size()
    {
        return _children.size();
    }

    @Override
    public Iterator<JsonNode> elements()
    {
        return _children.iterator();
    }

    @Override
    public JsonNode get(final int index)
    {
        if (index < 0)
            return null;
        if (index >= _children.size())
            return null;
        return _children.get(index);
    }

    @Override
    public JsonNode get(final String fieldName)
    {
        return null;
    }

    @Override
    public JsonNode path(final String fieldName)
    {
        return MissingNode.getInstance();
    }

    @Override
    public JsonNode path(final int index)
    {
        if (index < 0)
            return MissingNode.getInstance();
        if (index >= _children.size())
            return MissingNode.getInstance();
        return _children.get(index);
    }

    @Override
    public JsonNode set(final int index, final JsonNode value)
    {
        return _set(index, value == null ? nullNode() : value);
    }

    @Override
    public JsonArray add(final JsonNode value)
    {
        final JsonNode node = value == null ? nullNode() : value;
        _children.add(node);
        return this;
    }

    @Override
    public JsonArray addAll(final ArrayNode other)
    {
        _children.addAll(Lists.newArrayList(other));
        return this;
    }

    @Override
    public JsonArray addAll(final Collection<JsonNode> nodes)
    {
        _children.addAll(nodes);
        return this;
    }

    @Override
    public JsonArray insert(final int index, final JsonNode value)
    {
        doInsert(index, value == null ? nullNode() : value);
        return this;
    }

    @Override
    public JsonNode remove(final int index)
    {
        if (index < 0)
            return null;
        if (index >= _children.size())
            return null;
        return _children.remove(index);
    }

    @Override
    public JsonArray removeAll()
    {
        _children.clear();
        return this;
    }

    @Override
    public ArrayNode addArray()
    {
        final ArrayNode n = arrayNode();
        _children.add(n);
        return n;
    }

    @Override
    public ObjectNode addObject()
    {
        final ObjectNode n = objectNode();
        _children.add(n);
        return n;
    }

    @Override
    public JsonArray addPOJO(final Object value)
    {
        _children.add(value == null ? nullNode() : POJONode(value));
        return this;
    }

    @Override
    public JsonArray addNull()
    {
        _children.add(nullNode());
        return this;
    }

    @Override
    public JsonArray add(final int v)
    {
        _children.add(numberNode(v));
        return this;
    }

    @Override
    public JsonArray add(final Integer value)
    {
        _children.add(value == null ? nullNode() : numberNode(value));
        return this;
    }

    @Override
    public JsonArray add(final long v)
    {
        _children.add(numberNode(v));
        return this;
    }

    @Override
    public JsonArray add(final Long value)
    {
        _children.add(value == null ? nullNode() : numberNode(value));
        return this;
    }

    @Override
    public JsonArray add(final float v)
    {
        _children.add(numberNode(v));
        return this;
    }

    @Override
    public JsonArray add(final Float value)
    {
        _children.add(value == null ? nullNode() : numberNode(value));
        return this;
    }

    @Override
    public JsonArray add(final double v)
    {
        _children.add(numberNode(v));
        return this;
    }

    @Override
    public JsonArray add(final Double value)
    {
        _children.add(value == null ? nullNode() : numberNode(value));
        return this;
    }

    @Override
    public JsonArray add(final BigDecimal v)
    {
        _children.add(v == null ? nullNode() : numberNode(v));
        return this;
    }

    @Override
    public JsonArray add(final String v)
    {
        _children.add(v == null ? nullNode() : textNode(v));
        return this;
    }

    @Override
    public JsonArray add(final boolean v)
    {
        _children.add(booleanNode(v));
        return this;
    }

    @Override
    public JsonArray add(final Boolean value)
    {
        _children.add(value == null ? nullNode() : booleanNode(value));
        return this;
    }

    @Override
    public JsonArray add(final byte[] v)
    {
        _children.add(v == null ? nullNode() : binaryNode(v));
        return this;
    }

    @Override
    public ArrayNode insertArray(final int index)
    {
        final ArrayNode n = arrayNode();
        doInsert(index, n);
        return n;
    }

    @Override
    public ObjectNode insertObject(final int index)
    {
        final ObjectNode n = objectNode();
        doInsert(index, n);
        return n;
    }

    @Override
    public JsonArray insertPOJO(final int index, final Object value)
    {
        doInsert(index, value == null ? nullNode() : POJONode(value));
        return this;
    }

    @Override
    public JsonArray insertNull(final int index)
    {
        doInsert(index, nullNode());
        return this;
    }

    @Override
    public JsonArray insert(final int index, final int v)
    {
        doInsert(index, numberNode(v));
        return this;
    }

    @Override
    public JsonArray insert(final int index, final Integer value)
    {
        doInsert(index, value == null ? nullNode() : numberNode(value));
        return this;
    }

    @Override
    public JsonArray insert(final int index, final long v)
    {
        doInsert(index, numberNode(v));
        return this;
    }

    @Override
    public JsonArray insert(final int index, final Long value)
    {
        doInsert(index, value == null ? nullNode() : numberNode(value));
        return this;
    }

    @Override
    public JsonArray insert(final int index, final float v)
    {
        doInsert(index, numberNode(v));
        return this;
    }

    @Override
    public JsonArray insert(final int index, final Float value)
    {
        doInsert(index, value == null ? nullNode() : numberNode(value));
        return this;
    }

    @Override
    public JsonArray insert(final int index, final double v)
    {
        doInsert(index, numberNode(v));
        return this;
    }

    @Override
    public JsonArray insert(final int index, final Double value)
    {
        doInsert(index, value == null ? nullNode() : numberNode(value));
        return this;
    }

    @Override
    public JsonArray insert(final int index, final BigDecimal v)
    {
        doInsert(index, v == null ? nullNode() : numberNode(v));
        return this;
    }

    @Override
    public JsonArray insert(final int index, final String v)
    {
        doInsert(index, v == null ? nullNode() : textNode(v));
        return this;
    }

    @Override
    public JsonArray insert(final int index, final boolean v)
    {
        doInsert(index, booleanNode(v));
        return this;
    }

    @Override
    public JsonArray insert(final int index, final Boolean value)
    {
        doInsert(index, value == null ? nullNode() : booleanNode(value));
        return this;
    }

    @Override
    public JsonArray insert(final int index, final byte[] v)
    {
        doInsert(index, v == null ? nullNode() : binaryNode(v));
        return this;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (o == null)
            return false;
        if (this == o)
            return true;
        if (getClass() != o.getClass())
            return false;
        final JsonArray other = (JsonArray) o;
        return _children.equals(other._children);
    }

    @Override
    public int hashCode()
    {
        return _children.hashCode();
    }


    @Override
    public String toString()
    {
        return '[' + JOINER.join(_children) + ']';
    }

    @Override
    public JsonNode _set(final int index, final JsonNode value)
    {
        if (index < 0 || index >= _children.size())
            throw new IndexOutOfBoundsException("Illegal index " + index
                + ", array size " + size());
        return _children.set(index, value);
    }

    private void doInsert(final int index, final JsonNode node)
    {
        if (index < 0)
            _children.add(0, node);
        else if (index >= _children.size())
            _children.add(node);
        else
            _children.add(index, node);
    }
}
