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
import com.google.common.collect.Maps;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Override of Jackson's {@code ObjectNode}
 *
 * <p>We override it so that the member map is always created instead of playing
 * around with {@code null} when the object has no entries. It simplifies the
 * code quite a lot. Also, make it {@code final}.</p>
 */
public final class JsonObject
    extends ObjectNode
{
    public JsonObject(final JsonNodeFactory nc)
    {
        super(nc);
        _children = Maps.newHashMap();
    }

    @SuppressWarnings("unchecked")
    @Override
    public JsonObject deepCopy()
    {
        final JsonObject ret = new JsonObject(_nodeFactory);
        for (final Map.Entry<String, JsonNode> entry: _children.entrySet())
            ret._children.put(entry.getKey(), entry.getValue().deepCopy());
        return ret;
    }

    /*
    /**********************************************************
    /* Implementation of core JsonNode API
    /**********************************************************
     */

    @Override
    public int size()
    {
        return _children.size();
    }

    @Override
    public Iterator<JsonNode> elements()
    {
        return _children.values().iterator();
    }

    @Override
    public JsonNode get(final String fieldName)
    {
        return _children.get(fieldName);
    }

    @Override
    public Iterator<String> fieldNames()
    {
        return _children.keySet().iterator();
    }

    @Override
    public JsonNode path(final String fieldName)
    {
        final JsonNode ret = _children.get(fieldName);
        return ret == null ? MissingNode.getInstance() : ret;
    }

    /**
     * Method to use for accessing all fields (with both names
     * and values) of this JSON Object.
     */
    @Override
    public Iterator<Map.Entry<String, JsonNode>> fields()
    {
        return _children.entrySet().iterator();
    }

    @Override
    public ObjectNode with(final String propertyName)
    {
        final JsonNode n = _children.get(propertyName);
        if (n != null) {
            if (n instanceof ObjectNode)
                return (ObjectNode) n;
            throw new UnsupportedOperationException("Property '" + propertyName
                + "' has value that is not of type ObjectNode (but " + n
                .getClass().getName() + ')');
        }
        final ObjectNode result = objectNode();
        _children.put(propertyName, result);
        return result;
    }

    @Override
    public ArrayNode withArray(final String propertyName)
    {
        final JsonNode n = _children.get(propertyName);
        if (n != null) {
            if (n instanceof ArrayNode)
                return (ArrayNode) n;
            throw new UnsupportedOperationException("Property '" + propertyName
                + "' has value that is not of type ArrayNode (but " + n
                .getClass().getName() + ')');
        }
        final ArrayNode result = arrayNode();
        _children.put(propertyName, result);
        return result;
    }
    
    /*
    /**********************************************************
    /* Extended ObjectNode API, mutators, since 2.1
    /**********************************************************
     */

    @Override
    public JsonNode set(final String fieldName, final JsonNode value)
    {
        _children.put(fieldName, value == null ? nullNode() : value);
        return this;
    }

    @Override
    public JsonNode setAll(final Map<String, JsonNode> properties)
    {
        JsonNode node;
        for (final Map.Entry<String, JsonNode> en: properties.entrySet()) {
            node = en.getValue();
            if (node == null)
                node = nullNode();
            _children.put(en.getKey(), node);
        }
        return this;
    }

    @Override
    public JsonNode setAll(final ObjectNode other)
    {
        _children.putAll(JacksonUtils.asMap(other));
        return this;
    }

    @Override
    public JsonNode replace(final String fieldName, final JsonNode value)
    {
        return _children.put(fieldName, value == null ? nullNode() : value);
    }

    @Override
    public JsonNode without(final String fieldName)
    {
        _children.remove(fieldName);
        return this;
    }

    @Override
    public JsonObject without(final Collection<String> fieldNames)
    {
        _children.keySet().removeAll(fieldNames);
        return this;
    }

    @Override
    public JsonNode put(final String fieldName, final JsonNode value)
    {
        return _children.put(fieldName, value == null ? nullNode() : value);
    }

    @Override
    public JsonNode remove(final String fieldName)
    {
        return _children.remove(fieldName);
    }

    @Override
    public JsonObject remove(final Collection<String> fieldNames)
    {
        _children.keySet().removeAll(fieldNames);
        return this;
    }

    @Override
    public JsonObject removeAll()
    {
        _children.clear();
        return this;
    }

    @Override
    public JsonNode putAll(final Map<String, JsonNode> properties)
    {
        return setAll(properties);
    }

    @Override
    public JsonNode putAll(final ObjectNode other)
    {
        return setAll(other);
    }

    @Override
    public JsonObject retain(final Collection<String> fieldNames)
    {
        _children.keySet().retainAll(fieldNames);
        return this;
    }

    @Override
    public JsonObject retain(final String... fieldNames)
    {
        return retain(Arrays.asList(fieldNames));
    }
    
    @Override
    public ArrayNode putArray(final String fieldName)
    {
        final ArrayNode n = arrayNode();
        _children.put(fieldName, n);
        return n;
    }

    @Override
    public ObjectNode putObject(final String fieldName)
    {
        final ObjectNode n = objectNode();
        _children.put(fieldName, n);
        return n;
    }

    @Override
    public JsonObject putPOJO(final String fieldName, final Object pojo)
    {
        _children.put(fieldName, POJONode(pojo));
        return this;
    }

    @Override
    public JsonObject putNull(final String fieldName)
    {
        _children.put(fieldName, nullNode());
        return this;
    }

    @Override
    public JsonObject put(final String fieldName, final int v)
    {
        _children.put(fieldName, numberNode(v));
        return this;
    }

    @Override
    public JsonObject put(final String fieldName, final Integer value)
    {
        _children.put(fieldName, value == null ? nullNode() : numberNode(value));
        return this;
    }

    @Override
    public JsonObject put(final String fieldName, final long v)
    {
        _children.put(fieldName, numberNode(v));
        return this;
    }

    @Override
    public JsonObject put(final String fieldName, final Long value)
    {
        _children.put(fieldName, value == null ? nullNode() : numberNode(value));
        return this;
    }

    @Override
    public JsonObject put(final String fieldName, final float v)
    {
        _children.put(fieldName, numberNode(v));
        return this;
    }

    @Override
    public JsonObject put(final String fieldName, final Float value)
    {
        _children.put(fieldName, value == null ? nullNode() : numberNode(value));
        return this;
    }

    @Override
    public JsonObject put(final String fieldName, final double v)
    {
        _children.put(fieldName, numberNode(v));
        return this;
    }

    @Override
    public JsonObject put(final String fieldName, final Double value)
    {
        _children.put(fieldName, value == null ? nullNode() : numberNode(value));
        return this;
    }

    @Override
    public JsonObject put(final String fieldName, final BigDecimal v)
    {
        _children.put(fieldName, v == null ? nullNode() : numberNode(v));
        return this;
    }

    @Override
    public JsonObject put(final String fieldName, final String v)
    {
        _children.put(fieldName, v == null ? nullNode() : textNode(v));
        return this;
    }

    @Override
    public JsonObject put(final String fieldName, final boolean v)
    {
        _children.put(fieldName, booleanNode(v));
        return this;
    }

    @Override
    public JsonObject put(final String fieldName, final Boolean value)
    {
        _children.put(fieldName, value == null ? nullNode() : booleanNode(value));
        return this;
    }

    @Override
    public JsonObject put(final String fieldName, final byte[] v)
    {
        _children.put(fieldName, v == null ? nullNode() : binaryNode(v));
        return this;
    }

    @Override
    protected boolean _equals(final ObjectNode other)
    {
        return _children.equals(JacksonUtils.asMap(other));
    }

    @Override
    public int hashCode()
    {
        return _children.hashCode();
    }
}
