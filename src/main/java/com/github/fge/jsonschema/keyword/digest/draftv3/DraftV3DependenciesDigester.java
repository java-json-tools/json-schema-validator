/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.keyword.digest.draftv3;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.keyword.digest.AbstractDigester;
import com.github.fge.jsonschema.keyword.digest.Digester;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

/**
 * Digester for {@code dependencies}
 *
 * <p>This stores property dependencies separately from schema dependencies.</p>
 */
public final class DraftV3DependenciesDigester
    extends AbstractDigester
{
    private static final Digester INSTANCE = new DraftV3DependenciesDigester();

    public static Digester getInstance()
    {
        return INSTANCE;
    }

    private DraftV3DependenciesDigester()
    {
        super("dependencies", NodeType.OBJECT);
    }

    @Override
    public JsonNode digest(final JsonNode schema)
    {
        final ObjectNode ret = FACTORY.objectNode();

        final ObjectNode propertyDeps = FACTORY.objectNode();
        ret.put("propertyDeps", propertyDeps);

        final ArrayNode schemaDeps = FACTORY.arrayNode();
        ret.put("schemaDeps", schemaDeps);

        final List<String> list = Lists.newArrayList();

        final Map<String, JsonNode> map
            = JacksonUtils.asMap(schema.get(keyword));

        String key;
        JsonNode value;
        NodeType type;

        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();
            type = NodeType.getNodeType(value);
            switch (type) {
                case OBJECT:
                    list.add(key);
                    break;
                case ARRAY:
                    final JsonNode node = sortedSet(value);
                    if (node.size() != 0)
                        propertyDeps.put(key, node);
                    break;
                case STRING:
                    propertyDeps.put(key, FACTORY.arrayNode()
                        .add(value.textValue()));
            }
        }

        for (final String s: Ordering.natural().sortedCopy(list))
            schemaDeps.add(s);

        return ret;
    }

    private static JsonNode sortedSet(final JsonNode node)
    {
        final SortedSet<JsonNode> set
            = Sets.newTreeSet(new Comparator<JsonNode>()
            {
                @Override
                public int compare(final JsonNode o1, final JsonNode o2)
                {
                    return o1.textValue().compareTo(o2.textValue());
                }
            });

        set.addAll(Sets.newHashSet(node));
        final ArrayNode ret = FACTORY.arrayNode();
        ret.addAll(set);
        return ret;
    }
}
