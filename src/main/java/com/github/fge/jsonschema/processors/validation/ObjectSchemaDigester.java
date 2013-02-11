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

package com.github.fge.jsonschema.processors.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.keyword.digest.AbstractDigester;
import com.github.fge.jsonschema.util.Digester;
import com.github.fge.jsonschema.util.NodeType;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import java.util.Set;

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
