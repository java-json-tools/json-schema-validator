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

package com.github.fge.jsonschema.processing.digest;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.keyword.digest.Digester;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.processing.ValidationDigest;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.NodeType;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

public final class SchemaDigester
    implements Processor<ValidationData, ValidationDigest>
{
    private final ListMultimap<NodeType, String> typeMap
        = ArrayListMultimap.create();
    private final Map<String, Digester> digesterMap
        = Maps.newHashMap();
    private final LoadingCache<JsonNode, Map<String, JsonNode>> cache;

    public SchemaDigester(final Dictionary<Digester> dict)
    {
        String keyword;
        Digester digester;

        for (final Map.Entry<String, Digester> entry: dict.entries()) {
            keyword = entry.getKey();
            digester = entry.getValue();
            digesterMap.put(keyword, digester);
            for (final NodeType type: digester.supportedTypes())
                typeMap.put(type, keyword);
        }

        cache = CacheBuilder.newBuilder().build(loader());
    }

    @Override
    public ValidationDigest process(final ProcessingReport report,
        final ValidationData input)
        throws ProcessingException
    {
        final JsonNode schema = input.getSchema().getCurrentNode();
        final NodeType type
            = NodeType.getNodeType(input.getInstance().getCurrentNode());
        final Map<String, JsonNode> map = cache.getUnchecked(schema);
        map.keySet().retainAll(typeMap.get(type));
        return new ValidationDigest(input, ImmutableMap.copyOf(map));
    }

    private CacheLoader<JsonNode, Map<String, JsonNode>> loader()
    {
        return new CacheLoader<JsonNode, Map<String, JsonNode>>()
        {
            @Override
            public Map<String, JsonNode> load(final JsonNode key)
                throws ProcessingException
            {
                final Set<String> keywords = Sets.newHashSet(key.fieldNames());
                final Map<String, JsonNode> ret = Maps.newHashMap();

                keywords.retainAll(digesterMap.keySet());
                for (final String keyword: keywords)
                    ret.put(keyword, digesterMap.get(keyword).digest(key));

                return ret;
            }
        };
    }
}
