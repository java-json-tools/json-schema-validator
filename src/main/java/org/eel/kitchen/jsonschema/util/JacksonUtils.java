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

package org.eel.kitchen.jsonschema.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * <p>A small set of utility methods over Jackson.</p>
 */

public final class JacksonUtils
{
    private static final JsonNode EMPTY_SCHEMA = EmptySchema.getInstance();

    private JacksonUtils()
    {
    }

    /**
     * Return a map out of an object instance
     *
     * @param node the node
     * @return a mutable map made of the instance's entries
     */
    public static Map<String, JsonNode> nodeToMap(final JsonNode node)
    {
        final Map<String, JsonNode> ret = Maps.newHashMap();

        final Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();

        Map.Entry<String, JsonNode> entry;
        while (iterator.hasNext()) {
            entry = iterator.next();
            ret.put(entry.getKey(), entry.getValue());
        }

        return ret;
    }

    /**
     * Return a sorted map out of an object instance
     *
     * <p>This is used by syntax validation especially: it is more convenient to
     * present validation messages in key order.</p>
     *
     * @param node the node
     * @return a mutable map made of the instance's entries
     */
    public static SortedMap<String, JsonNode> nodeToTreeMap(
        final JsonNode node)
    {
        final SortedMap<String, JsonNode> ret = Maps.newTreeMap();

        final Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();

        Map.Entry<String, JsonNode> entry;
        while (iterator.hasNext()) {
            entry = iterator.next();
            ret.put(entry.getKey(), entry.getValue());
        }

        return ret;
    }

    /**
     * Return a set of field names in an object instance
     *
     * @param node the node
     * @return a mutable set of the instance's keys
     */
    public static Set<String> fieldNames(final JsonNode node)
    {
        final Set<String> ret = Sets.newHashSet();

        final Iterator<String> iterator = node.fieldNames();

        while (iterator.hasNext())
            ret.add(iterator.next());

        return ret;
    }

    /**
     * Return an empty schema
     *
     * <p><b>IMPORTANT:</b> due to the way {@code equals()} is implemented in
     * Jackson's {@link ObjectNode}, the {@link JsonNode} returned by this
     * method <b>is not</b> equal to an empty {@link ObjectNode}.</p>
     *
     * @return a statically created, empty, JSON object.
     */
    public static JsonNode emptySchema()
    {
        return EMPTY_SCHEMA;
    }

    private static final class EmptySchema
        extends ObjectNode
    {
        private static final JsonNode instance = new EmptySchema();

        private static JsonNode getInstance()
        {
            return instance;
        }

        private EmptySchema()
        {
            super(JsonNodeFactory.instance);
        }

        @Override
        public ObjectNode with(final String propertyName)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public ArrayNode withArray(final String propertyName)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public JsonNode put(final String fieldName, final JsonNode value)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public JsonNode putAll(final Map<String, JsonNode> properties)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public JsonNode putAll(final ObjectNode other)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public ArrayNode putArray(final String fieldName)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectNode putObject(final String fieldName)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectNode putPOJO(final String fieldName, final Object pojo)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectNode putNull(final String fieldName)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectNode put(final String fieldName, final int v)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectNode put(final String fieldName, final Integer value)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectNode put(final String fieldName, final long v)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectNode put(final String fieldName, final Long value)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectNode put(final String fieldName, final float v)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectNode put(final String fieldName, final Float value)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectNode put(final String fieldName, final double v)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectNode put(final String fieldName, final Double value)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectNode put(final String fieldName, final BigDecimal v)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectNode put(final String fieldName, final String v)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectNode put(final String fieldName, final boolean v)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectNode put(final String fieldName, final Boolean value)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectNode put(final String fieldName, final byte[] v)
        {
            throw new UnsupportedOperationException();
        }
    }
}
