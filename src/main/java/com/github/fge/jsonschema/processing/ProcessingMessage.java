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

package com.github.fge.jsonschema.processing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.github.fge.jsonschema.tree.JsonTree;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import com.google.common.collect.Maps;

import java.util.Map;

public final class ProcessingMessage
{
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

    private final Map<String, JsonNode> map = Maps.newLinkedHashMap();

    public ProcessingMessage(final JsonTree tree)
    {
        put("pointer", tree.getCurrentPointer());
    }

    public ProcessingMessage(final JsonSchemaTree schemaTree,
        final JsonTree tree)
    {
        this(tree);
        final ObjectNode schemaInfo = FACTORY.objectNode();

        schemaInfo.put("location", schemaTree.getLoadingRef().toString());
        schemaInfo.put("pointer", schemaTree.getCurrentPointer().toString());
        schemaInfo.put("uriContext", schemaTree.getCurrentRef().toString());
        put("schema", schemaInfo);
    }

    public ObjectNode asJson()
    {
        final ObjectNode ret = FACTORY.objectNode();
        ret.putAll(map);
        return ret;
    }

    public ProcessingMessage put(final String key, final JsonNode value)
    {
        map.put(key, value);
        return this;
    }

    public <T> ProcessingMessage put(final String key, final T value)
    {
        map.put(key, FACTORY.textNode(value.toString()));
        return this;
    }

    @Override
    public String toString()
    {
        return map.toString();
    }
}
