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

package com.github.fge.jsonschema.processors.digest;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.keyword.digest.Digester;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.library.Library;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processors.data.SchemaContext;
import com.github.fge.jsonschema.processors.data.SchemaDigest;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.NodeType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;

public final class SchemaDigester
    implements Processor<SchemaContext, SchemaDigest>
{
    private final ListMultimap<NodeType, String> typeMap
        = ArrayListMultimap.create();
    private final Map<String, Digester> digesterMap = Maps.newHashMap();

    public SchemaDigester(final Library library)
    {
        this(library.getDigesters());
    }

    public SchemaDigester(final Dictionary<Digester> dict)
    {
        String keyword;
        Digester digester;

        final Map<String, Digester> map = dict.entries();

        for (final Map.Entry<String, Digester> entry: map.entrySet()) {
            keyword = entry.getKey();
            digester = entry.getValue();
            digesterMap.put(keyword, digester);
            for (final NodeType type: digester.supportedTypes())
                typeMap.put(type, keyword);
        }
    }

    @Override
    public SchemaDigest process(final ProcessingReport report,
        final SchemaContext input)
        throws ProcessingException
    {
        final JsonNode schema = input.getSchema().getNode();
        final NodeType type = input.getInstanceType();
        final Map<String, JsonNode> map = Maps.newHashMap(buildDigests(schema));
        map.keySet().retainAll(typeMap.get(type));
        return new SchemaDigest(input, map);
    }

    private Map<String, JsonNode> buildDigests(final JsonNode schema)
    {
        final ImmutableMap.Builder<String, JsonNode> builder
            = ImmutableMap.builder();
        final Map<String, Digester> map = Maps.newHashMap(digesterMap);

        map.keySet().retainAll(Sets.newHashSet(schema.fieldNames()));

        for (final Map.Entry<String, Digester> entry: map.entrySet())
            builder.put(entry.getKey(), entry.getValue().digest(schema));

        return builder.build();
    }

    @Override
    public String toString()
    {
        return "schema digester";
    }
}
