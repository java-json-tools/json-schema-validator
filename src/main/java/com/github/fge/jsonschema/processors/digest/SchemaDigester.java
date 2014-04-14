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

package com.github.fge.jsonschema.processors.digest;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.util.Dictionary;
import com.github.fge.jsonschema.keyword.digest.Digester;
import com.github.fge.jsonschema.library.Library;
import com.github.fge.jsonschema.processors.data.SchemaContext;
import com.github.fge.jsonschema.processors.data.SchemaDigest;
import com.github.fge.jsonschema.processors.validation.ValidationChain;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;

/**
 * The schema digester
 *
 * <p>This processor is called by a {@link ValidationChain} after it has made
 * sure that the schema is syntactically valid.</p>
 */
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
