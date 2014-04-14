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

package com.github.fge.jsonschema.processors.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.core.exceptions.InvalidSchemaException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.processing.CachingProcessor;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.JsonTree;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.processors.data.SchemaContext;
import com.github.fge.jsonschema.processors.data.ValidatorList;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

/**
 * Main validation processor
 */
public final class ValidationProcessor
    implements Processor<FullData, FullData>
{
    private final MessageBundle syntaxMessages;
    private final MessageBundle validationMessages;
    private final Processor<SchemaContext, ValidatorList> processor;
    private final LoadingCache<JsonNode, ArraySchemaSelector> arrayCache;
    private final LoadingCache<JsonNode, ObjectSchemaSelector> objectCache;

    public ValidationProcessor(final ValidationConfiguration cfg,
        final Processor<SchemaContext, ValidatorList> processor)
    {
        syntaxMessages = cfg.getSyntaxMessages();
        validationMessages = cfg.getValidationMessages();
        this.processor = new CachingProcessor<SchemaContext, ValidatorList>(
            processor, SchemaContextEquivalence.getInstance()
        );
        arrayCache = CacheBuilder.newBuilder().build(arrayLoader());
        objectCache = CacheBuilder.newBuilder().build(objectLoader());
    }

    @Override
    public FullData process(final ProcessingReport report,
        final FullData input)
        throws ProcessingException
    {
        /*
         * Build a validation context, attach a report to it
         */
        final SchemaContext context = new SchemaContext(input);

        /*
         * Get the full context from the cache. Inject the messages into the
         * main report.
         */
        final ValidatorList fullContext = processor.process(report, context);

        if (fullContext == null)
            throw new InvalidSchemaException(new ProcessingMessage()
                .setMessage(syntaxMessages.getMessage("core.invalidSchema")));

        /*
         * Get the calculated context. Build the data.
         */
        final SchemaContext newContext = fullContext.getContext();
        final FullData data = new FullData(newContext.getSchema(),
            input.getInstance(), input.isDeepCheck());

        /*
         * Validate against all keywords.
         */
        for (final KeywordValidator validator: fullContext)
            validator.validate(this, report, validationMessages, data);

        /*
         * At that point, if the report is a failure, we quit: there is no
         * reason to go any further. Unless the user has asked to continue even
         * in this case.
         */
        if (!(report.isSuccess() || data.isDeepCheck()))
            return input;

        /*
         * Now check whether this is a container node with a size greater than
         * 0. If not, no need to go see the children.
         */
        final JsonNode node = data.getInstance().getNode();
        if (node.size() == 0)
            return input;

        if (node.isArray())
            processArray(report, data);
        else
            processObject(report, data);

        return input;
    }

    private void processArray(final ProcessingReport report,
        final FullData input)
        throws ProcessingException
    {
        final SchemaTree tree = input.getSchema();
        final JsonTree instance = input.getInstance();

        final JsonNode schema = tree.getNode();
        final JsonNode node = instance.getNode();

        final JsonNode digest = ArraySchemaDigester.getInstance().digest(schema);
        final ArraySchemaSelector selector = arrayCache.getUnchecked(digest);

        final int size = node.size();

        FullData data;
        JsonTree newInstance;

        for (int index = 0; index < size; index++) {
            newInstance = instance.append(JsonPointer.of(index));
            data = input.withInstance(newInstance);
            for (final JsonPointer ptr: selector.selectSchemas(index)) {
                data = data.withSchema(tree.append(ptr));
                process(report, data);
            }
        }
    }

    private void processObject(final ProcessingReport report,
        final FullData input)
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

        FullData data;
        JsonTree newInstance;

        for (final String field: fields) {
            newInstance = instance.append(JsonPointer.of(field));
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
