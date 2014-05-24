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

package com.github.fge.jsonschema.processors.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.keyword.digest.AbstractDigester;
import com.github.fge.jsonschema.keyword.digest.Digester;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * JSON Schema digester for an {@link ObjectSchemaSelector}
 */
public final class ObjectSchemaDigester
    extends AbstractDigester
{
    private static final Digester INSTANCE = new ObjectSchemaDigester();

    public static Digester getInstance()
    {
        return INSTANCE;
    }

    private ObjectSchemaDigester()
    {
        super("", NodeType.OBJECT);
    }

    @Override
    public JsonNode digest(final JsonNode schema)
    {
        final ObjectNode ret = FACTORY.objectNode();
        ret.put("hasAdditional", schema.path("additionalProperties").isObject());

        Set<String> set;

        ArrayNode node;

        node = FACTORY.arrayNode();
        ret.put("properties", node);

        set = Sets.newHashSet(schema.path("properties").fieldNames());
        for (final String field: Ordering.natural().sortedCopy(set))
            node.add(field);

        node = FACTORY.arrayNode();
        ret.put("patternProperties", node);

        set = Sets.newHashSet(schema.path("patternProperties").fieldNames());
        for (final String field: Ordering.natural().sortedCopy(set))
            node.add(field);

        return ret;
    }
}
