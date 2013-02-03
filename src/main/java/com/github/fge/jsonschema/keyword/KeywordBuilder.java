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
import com.google.common.base.Equivalence;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class KeywordBuilder
    implements Processor<ValidationData, KeywordSet>
{
    private final Dictionary<KeywordDescriptor> dict;
    private final Map<String, ProcessingCache<JsonNode, KeywordValidator>> caches;

    public KeywordBuilder(final Dictionary<KeywordDescriptor> dict)
    {
        this.dict = dict;
        final ImmutableMap.Builder<String, ProcessingCache<JsonNode, KeywordValidator>>
            cachesBuilder = ImmutableMap.builder();

        String keyword;
        KeywordDescriptor descriptor;
        ProcessingCache<JsonNode, KeywordValidator> processingCache;

        for (final Map.Entry<String, KeywordDescriptor> entry: dict.entries()) {
            keyword = entry.getKey();
            descriptor = entry.getValue();
            processingCache = new ProcessingCache<JsonNode, KeywordValidator>(
                descriptor.equivalence, keywordLoader(descriptor));
            cachesBuilder.put(keyword, processingCache);
        }

        caches = cachesBuilder.build();
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
        final JsonNode schema = input.getSchema().getCurrentNode();
        final JsonNode instance = input.getInstance().getCurrentNode();

        /*
         * Collect field names in the schema; only retain defined ones
         */
        final Set<String> fields = Sets.newHashSet(schema.fieldNames());
        fields.retainAll(caches.keySet());

        /*
         * Grab the type of the instance to validate
         */
        final NodeType type = NodeType.getNodeType(instance);

        /*
         * The list of built keywords
         */
        final List<KeywordValidator> list = Lists.newArrayList();

        for (final String keyword: fields)
            if (dict.get(keyword).types.contains(type))
                list.add(caches.get(keyword).get(schema));

        return new KeywordSet(list);
    }

    private static CacheLoader<Equivalence.Wrapper<JsonNode>, KeywordValidator>
        keywordLoader(final KeywordDescriptor descriptor)
    {
        return new CacheLoader<Equivalence.Wrapper<JsonNode>, KeywordValidator>()
        {
            @Override
            public KeywordValidator load(final Equivalence.Wrapper<JsonNode> key)
                throws ProcessingException
            {
                try {
                    return descriptor.constructor.newInstance(key.get());
                } catch (InstantiationException e) {
                    throw new ProcessingException(
                        "failed to instansiate validator", e);
                } catch (IllegalAccessException e) {
                    throw new ProcessingException(
                        "permission problem, cannot instansiate", e);
                } catch (InvocationTargetException e) {
                    throw new ProcessingException(
                        "failed to invoke constructor for validator", e);
                }
            }
        };
    }
}
