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
import com.github.fge.jsonschema.processing.ProcessingCache;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processors.data.FullValidationContext;
import com.github.fge.jsonschema.processors.data.ValidationContext;
import com.github.fge.jsonschema.processors.data.ValidationData;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.JsonTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.google.common.base.Equivalence;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public final class ValidationProcessor
    implements Processor<ValidationData, ProcessingReport>
{
    private static final ThreadLocal<ProcessingReport> REPORT
        = new ThreadLocal<ProcessingReport>();

    private final ProcessingCache<ValidationContext, FullValidationContext> cache;
    private final Processor<ValidationContext, FullValidationContext> processor;
    private final LoadingCache<JsonNode, ArraySchemaSelector> arrayCache;
    private final LoadingCache<JsonNode, ObjectSchemaSelector> objectCache;

    public ValidationProcessor(
        final Processor<ValidationContext, FullValidationContext> processor)
    {
        this.processor = processor;
        arrayCache = CacheBuilder.newBuilder().build(arrayLoader());
        objectCache = CacheBuilder.newBuilder().build(objectLoader());
        cache = new ProcessingCache<ValidationContext, FullValidationContext>(
            ValidationContextEquivalence.getInstance(), loader()
        );
    }

    @Override
    public ProcessingReport process(final ProcessingReport report,
        final ValidationData input)
        throws ProcessingException
    {
        /*
         * Build a validation context, attach a report to it
         */
        final ValidationContext context = new ValidationContext(input);

        REPORT.set(report);
        /*
         * Get the full context from the cache. Inject the messages into the
         * main report.
         */
        final FullValidationContext fullContext = cache.get(context);

        /*
         * Get the calculated context. Build the data.
         */
        final ValidationContext newContext = fullContext.getContext();
        final ValidationData data = new ValidationData(newContext.getSchema(),
            input.getInstance());

        /*
         * Validate against all keywords.
         */
        for (final KeywordValidator validator: fullContext)
            validator.validate(this, report, data);

        /*
         * At that point, if the report is a failure, we quit: there is no
         * reason to go any further.
         */
        if (!report.isSuccess())
            return report;

        /*
         * Now check whether this is a container node with a size greater than
         * 0. If not, no need to go see the children.
         */
        final JsonNode node = data.getInstance().getNode();
        if (node.size() == 0)
            return report;

        if (node.isArray())
            processArray(report, data);
        else
            processObject(report, data);

        return report;
    }

    private void processArray(final ProcessingReport report,
        final ValidationData input)
        throws ProcessingException
    {
        final SchemaTree tree = input.getSchema();
        final JsonTree instance = input.getInstance();

        final JsonNode schema = tree.getNode();
        final JsonNode node = instance.getNode();

        final JsonNode digest = ArraySchemaDigester.getInstance().digest(schema);
        final ArraySchemaSelector selector = arrayCache.getUnchecked(digest);

        final int size = node.size();

        ValidationData data;
        JsonTree newInstance;

        for (int index = 0; index < size; index++) {
            newInstance = instance.append(JsonPointer.empty().append(index));
            data = input.withInstance(newInstance);
            for (final JsonPointer ptr: selector.selectSchemas(index)) {
                data = data.withSchema(tree.append(ptr));
                process(report, data);
            }
        }
    }

    private void processObject(final ProcessingReport report,
        final ValidationData input)
        throws ProcessingException
    {
        final SchemaTree tree = input.getSchema();
        final JsonTree instance = input.getInstance();

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
            data = input.withInstance(newInstance);
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

    private CacheLoader<Equivalence.Wrapper<ValidationContext>, FullValidationContext>
        loader()
    {
        return new CacheLoader<Equivalence.Wrapper<ValidationContext>, FullValidationContext>()
        {
            @Override
            public FullValidationContext load(
                final Equivalence.Wrapper<ValidationContext> key)
                throws ProcessingException
            {
                final ValidationContext context = key.get();
                return processor.process(REPORT.get(), context);
            }
        };
    }
    @Override
    public String toString()
    {
        return "validation processor";
    }
}
