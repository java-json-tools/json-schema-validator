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

package com.github.fge.jsonschema.keyword.equivalence.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.keyword.equivalence.KeywordEquivalence;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public final class AdditionalPropertiesEquivalence
    extends KeywordEquivalence
{
    private static final KeywordEquivalence INSTANCE
        = new AdditionalPropertiesEquivalence();

    public static KeywordEquivalence getInstance()
    {
        return INSTANCE;
    }

    private AdditionalPropertiesEquivalence()
    {
        super("additionalProperties", "properties", "patternProperties");
    }

    @Override
    protected JsonNode digestedNode(final JsonNode orig)
    {
        final ObjectNode ret = FACTORY.objectNode();
        ret.put(keyword, true);

        if (orig.get(keyword).asBoolean(true))
            return ret;

        final List<String> properties
            = Lists.newArrayList(orig.path("properties").fieldNames());
        final List<String> patternProperties
            = Lists.newArrayList(orig.path("patternProperties").fieldNames());

        Collections.sort(properties);
        Collections.sort(patternProperties);

        ArrayNode node;

        node = FACTORY.arrayNode();
        for (final String field: properties)
            node.add(field);
        ret.put("properties", node);

        node = FACTORY.arrayNode();
        for (final String field: patternProperties)
            node.add(field);
        ret.put("patternProperties", node);

        return ret;
    }
}
