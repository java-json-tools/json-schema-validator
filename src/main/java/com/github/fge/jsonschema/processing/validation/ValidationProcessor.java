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

package com.github.fge.jsonschema.processing.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.processing.build.FullValidationContext;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.github.fge.jsonschema.tree.JsonTree;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public final class ValidationProcessor
    implements Processor<ValidationData, ProcessingReport>
{
    private final Processor<ValidationData, FullValidationContext> processor;
    private final LoadingCache<JsonNode, ArraySchemaSelector> arrayCache;
    private final LoadingCache<JsonNode, ObjectSchemaSelector> objectCache;

    public ValidationProcessor(
        final Processor<ValidationData, FullValidationContext> processor)
    {
        this.processor = processor;
        arrayCache = CacheBuilder.newBuilder().recordStats()
            .build(arrayLoader());
        objectCache = CacheBuilder.newBuilder().recordStats()
            .build(objectLoader());
    }

    @Override
    public ProcessingReport process(final ProcessingReport report,
        final ValidationData input)
        throws ProcessingException
    {
        final FullValidationContext context = processor.process(report, input);
        final ValidationData data = context.getValidationData();

        for (final KeywordValidator validator: context)
            validator.validate(this, report, data);

        final JsonNode node = input.getInstance().getCurrentNode();
        if (node.size() == 0)
            return report;

        if (node.isArray())
            processArray(report, input);
        else
            processObject(report, input);

        return report;
    }

    private void processArray(final ProcessingReport report,
        final ValidationData input)
        throws ProcessingException
    {
        final JsonSchemaTree schemaTree = input.getSchema();
        final JsonTree tree = input.getInstance();

        final JsonNode schema = schemaTree.getCurrentNode();
        final JsonNode instance = tree.getCurrentNode();

        final JsonNode digest = ArraySchemaDigester.getInstance().digest(schema);
        final ArraySchemaSelector selector = arrayCache.getUnchecked(digest);

        final int size = instance.size();

        for (int index = 0; index < size; index++) {
            tree.append(JsonPointer.empty().append(index));
            for (final JsonPointer ptr: selector.selectSchemas(index)) {
                schemaTree.append(ptr);
                process(report, input);
                schemaTree.pop();
            }
            tree.pop();
        }
    }

    private void processObject(final ProcessingReport report,
        final ValidationData input)
        throws ProcessingException
    {
        final JsonSchemaTree schemaTree = input.getSchema();
        final JsonTree tree = input.getInstance();

        final JsonNode schema = schemaTree.getCurrentNode();
        final JsonNode instance = tree.getCurrentNode();

        final JsonNode digest = ObjectSchemaDigester.getInstance()
            .digest(schema);
        final ObjectSchemaSelector selector = objectCache.getUnchecked(digest);

        final List<String> fields = Lists.newArrayList(instance.fieldNames());
        Collections.sort(fields);


        for (final String field: fields) {
            tree.append(JsonPointer.empty().append(field));
            for (final JsonPointer ptr: selector.selectSchemas(field)) {
                schemaTree.append(ptr);
                process(report, input);
                schemaTree.pop();
            }
            tree.pop();
        }
    }

    private static CacheLoader<JsonNode, ArraySchemaSelector> arrayLoader()
    {
        return new CacheLoader<JsonNode, ArraySchemaSelector>()
        {
            @Override
            public ArraySchemaSelector load(final JsonNode key)
                throws Exception
            {
                return new ArraySchemaSelector(key);
            }
        };
    }

    private static CacheLoader<JsonNode, ObjectSchemaSelector> objectLoader()
    {
        return new CacheLoader<JsonNode, ObjectSchemaSelector>()
        {
            @Override
            public ObjectSchemaSelector load(final JsonNode key)
                throws Exception
            {
                return new ObjectSchemaSelector(key);
            }
        };
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("objects: ").append(objectCache.stats().toString())
            .append('\n');
        sb.append("arrays: ").append(arrayCache.stats().toString())
            .append('\n');

        return sb.toString();
    }
}
