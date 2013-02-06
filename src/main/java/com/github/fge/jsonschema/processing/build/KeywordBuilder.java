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

package com.github.fge.jsonschema.processing.build;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ValidationDigest;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.ProcessingCache;
import com.github.fge.jsonschema.util.equivalence.JsonSchemaEquivalence;
import com.google.common.base.Equivalence;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Maps;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.SortedMap;

public final class KeywordBuilder
    implements Processor<ValidationDigest, FullValidationContext>
{
    private static final Equivalence<JsonNode> EQUIVALENCE
        = JsonSchemaEquivalence.getInstance();
    private static final String ERRMSG = "failed to build keyword validator";

    private final Map<String, ProcessingCache<JsonNode, KeywordValidator>> caches
        = Maps.newTreeMap();

    public KeywordBuilder(
        final Dictionary<Constructor<? extends KeywordValidator>> dict)
    {
        String key;
        ProcessingCache<JsonNode, KeywordValidator> processingCache;

        for (final Map.Entry<String, Constructor<? extends KeywordValidator>>
            entry: dict.entries()) {
            key = entry.getKey();
            processingCache = new ProcessingCache<JsonNode, KeywordValidator>(
                EQUIVALENCE, loader(entry.getValue()));
            caches.put(key, processingCache);
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
    public FullValidationContext process(final ProcessingReport report,
        final ValidationDigest input)
        throws ProcessingException
    {
        /*
         * Grab the schema and its fields
         */
        final JsonNode schema = input.getData().getSchema().getCurrentNode();
        final SortedMap<String, KeywordValidator> map = Maps.newTreeMap();


        String keyword;
        JsonNode digest;
        ProcessingCache<JsonNode, KeywordValidator> cache;
        KeywordValidator validator;

        for (final Map.Entry<String, JsonNode> entry:
            input.getDigests().entrySet()) {
            keyword = entry.getKey();
            digest = entry.getValue();
            cache = caches.get(keyword);
            validator = cache.get(digest);
            map.put(keyword, validator);
        }
        return new FullValidationContext(input.getData(), map.values());
    }

    private static CacheLoader<Equivalence.Wrapper<JsonNode>, KeywordValidator>
        loader(final Constructor<? extends KeywordValidator> constructor)
    {
        return new CacheLoader<Equivalence.Wrapper<JsonNode>, KeywordValidator>()
        {
            @Override
            public KeywordValidator load(final Equivalence.Wrapper<JsonNode> key)
                throws ProcessingException
            {
                try {
                    // FIXME: maybe the constructor should be built earlier?
                    // But on the other hand error handling is becoming hairy
                    return constructor.newInstance(key.get());
                } catch (InstantiationException e) {
                    throw new ProcessingException(ERRMSG, e);
                } catch (IllegalAccessException e) {
                    throw new ProcessingException(ERRMSG, e);
                } catch (InvocationTargetException e) {
                    throw new ProcessingException(ERRMSG, e);
                }
            }
        };
    }
}
