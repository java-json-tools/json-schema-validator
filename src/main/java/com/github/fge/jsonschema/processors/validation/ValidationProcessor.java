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
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processors.data.FullValidationContext;
import com.github.fge.jsonschema.processors.data.ValidationData;
import com.github.fge.jsonschema.processors.ref.RefResolverProcessor;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.JsonTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public final class ValidationProcessor
    implements Processor<ValidationData, ProcessingReport>
{
    private final RefResolverProcessor refResolver;
    private final Processor<ValidationData, FullValidationContext> processor;
    private final LoadingCache<JsonNode, ArraySchemaSelector> arrayCache;
    private final LoadingCache<JsonNode, ObjectSchemaSelector> objectCache;

    public ValidationProcessor(final RefResolverProcessor refResolver,
        final Processor<ValidationData, FullValidationContext> processor)
    {
        this.refResolver = refResolver;
        this.processor = processor;
        arrayCache = CacheBuilder.newBuilder().build(arrayLoader());
        objectCache = CacheBuilder.newBuilder().build(objectLoader());
    }


    @Override
    public ProcessingReport process(final ProcessingReport report,
        final ValidationData input)
        throws ProcessingException
    {
        final ValidationData resolved = refResolver.process(report, input);
        final FullValidationContext context
            = processor.process(report, resolved);
        final ValidationData data = context.getValidationData();

        for (final KeywordValidator validator: context)
            validator.validate(this, report, data);

        if (!report.isSuccess())
            return report;

        final JsonNode node = resolved.getInstance().getNode();
        if (node.size() == 0)
            return report;

        if (node.isArray())
            processArray(report, resolved);
        else
            processObject(report, resolved);

        return report;
    }

    private void processArray(final ProcessingReport report,
        final ValidationData resolved)
        throws ProcessingException
    {
        final SchemaTree tree = resolved.getSchema();
        final JsonTree instance = resolved.getInstance();

        final JsonNode schema = tree.getNode();
        final JsonNode node = instance.getNode();

        final JsonNode digest = ArraySchemaDigester.getInstance().digest(schema);
        final ArraySchemaSelector selector = arrayCache.getUnchecked(digest);

        final int size = node.size();

        ValidationData data;
        JsonTree newInstance;

        for (int index = 0; index < size; index++) {
            newInstance = instance.append(JsonPointer.empty().append(index));
            data = resolved.withInstance(newInstance);
            for (final JsonPointer ptr: selector.selectSchemas(index)) {
                data = data.withSchema(tree.append(ptr));
                process(report, data);
            }
        }
    }

    private void processObject(final ProcessingReport report,
        final ValidationData resolved)
        throws ProcessingException
    {
        final SchemaTree tree = resolved.getSchema();
        final JsonTree instance = resolved.getInstance();

        final JsonNode schema = tree.getNode();
        final JsonNode node = instance.getNode();

        final JsonNode digest = ObjectSchemaDigester.getInstance()
            .digest(schema);
        final ObjectSchemaSelector selector = objectCache.getUnchecked(digest);

        final List<String> fields = Lists.newArrayList(node.fieldNames());
        Collections.sort(fields);

        ValidationData data;
        JsonTree newInstance;

        for (final String field: fields) {
            newInstance = instance.append(JsonPointer.empty().append(field));
            data = resolved.withInstance(newInstance);
            for (final JsonPointer ptr: selector.selectSchemas(field)) {
                data = data.withSchema(tree.append(ptr));
                process(report, data);
            }
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
        return "validation processor";
    }
}
