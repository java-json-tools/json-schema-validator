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

package com.github.fge.jsonschema.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.util.ProcessingCache;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class KeywordBuilder
    implements Processor<ValidationData, KeywordSet>
{
    private final ListMultimap<NodeType, String> typeMap
        = ArrayListMultimap.create();
    private final Map<String, ProcessingCache<JsonNode, KeywordValidator>> caches
        = Maps.newTreeMap();

    public KeywordBuilder(final Dictionary<KeywordDescriptor> dict)
    {
        String key;
        KeywordDescriptor value;

        for (final Map.Entry<String, KeywordDescriptor> entry: dict.entries()) {
            key = entry.getKey();
            value = entry.getValue();
            for (final NodeType type: value.types)
                typeMap.put(type, key);
            caches.put(key, value.buildCache());
        }
    }

    /**
     * Process the input
     *
     * @param report the report to use while processing
     * @param input the input for this processor
     * @return the output
     * @throws ProcessingException processing failed
     */
    @Override
    public KeywordSet process(final ProcessingReport report,
        final ValidationData input)
        throws ProcessingException
    {
        /*
         * Grab the schema and its fields
         */
        final JsonNode schema = input.getSchema().getCurrentNode();
        final Set<String> fields = Sets.newHashSet(schema.fieldNames());

        /*
         * Grab the instance and its type
         */
        final JsonNode instance = input.getInstance().getCurrentNode();
        final NodeType type = NodeType.getNodeType(instance);

        /*
         * Only retain keywords that are known to validate this particular
         * instance type
         */
        fields.retainAll(typeMap.get(type));

        /*
         * Build the list
         */
        final List<KeywordValidator> list = Lists.newArrayList();

        for (final String keyword: fields)
            list.add(caches.get(keyword).get(schema));

        return new KeywordSet(list);
    }
}
