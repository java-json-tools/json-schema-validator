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

package org.eel.kitchen.jsonschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class SampleNodeProvider
{
    private static final JsonNodeFactory FACTORY = JsonNodeFactory.instance;
    private static final Map<NodeType, JsonNode> SAMPLE_DATA;

    private SampleNodeProvider()
    {
    }

    static {
        SAMPLE_DATA = Maps.newEnumMap(NodeType.class);

        SAMPLE_DATA.put(NodeType.ARRAY, FACTORY.arrayNode());
        SAMPLE_DATA.put(NodeType.BOOLEAN, FACTORY.booleanNode(true));
        SAMPLE_DATA.put(NodeType.INTEGER, FACTORY.numberNode(0));
        SAMPLE_DATA.put(NodeType.NULL, FACTORY.nullNode());
        SAMPLE_DATA.put(NodeType.NUMBER,
            FACTORY.numberNode(new BigDecimal("1.1")));
        SAMPLE_DATA.put(NodeType.OBJECT, FACTORY.objectNode());
        SAMPLE_DATA.put(NodeType.STRING, FACTORY.textNode(""));
    }

    public static Iterator<Object[]> getSamples(final NodeType first,
        final NodeType... other)
    {
        return doGetSamples(EnumSet.of(first, other));
    }

    public static Iterator<Object[]> getSamplesExcept(final NodeType first,
        final NodeType... other)
    {
        return doGetSamples(Sets.complementOf(EnumSet.of(first, other)));
    }

    private static Iterator<Object[]> doGetSamples(final Set<NodeType> types)
    {
        final Map<NodeType, JsonNode> map = Maps.newEnumMap(SAMPLE_DATA);
        map.keySet().retainAll(types);

        return FluentIterable.from(map.values())
            .transform(new Function<JsonNode, Object[]>()
            {
                @Override
                public Object[] apply(final JsonNode input)
                {
                    return new Object[] { input };
                }
            }).iterator();
    }
}
