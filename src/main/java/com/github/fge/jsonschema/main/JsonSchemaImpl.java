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

package com.github.fge.jsonschema.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.processing.ProcessingResult;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.report.ListProcessingReport;
import com.github.fge.jsonschema.core.report.MessageProvider;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.report.ReportProvider;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.core.tree.SimpleJsonTree;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.processors.validation.ValidationProcessor;

import javax.annotation.concurrent.Immutable;

/**
 * Single-schema instance validator
 *
 * <p>This is the class you will use the most often. It is, in essence, a {@link
 * JsonValidator} initialized with a single JSON Schema. Note however that this
 * class still retains the ability to resolve JSON References.</p>
 *
 * <p>It has no public constructors: you should use the appropriate methods in
 * {@link JsonSchemaFactory} to obtain an instance of this class.</p>
 */
@Immutable
final class JsonSchemaImpl implements JsonSchema
{
    private final ValidationProcessor processor;
    private final SchemaTree schema;
    private final ReportProvider reportProvider;

    /**
     * Package private constructor
     *
     * @param processor the validation processor
     * @param schema the schema to bind to this instance
     * @param reportProvider the report provider
     */
    JsonSchemaImpl(final ValidationProcessor processor, final SchemaTree schema,
                   final ReportProvider reportProvider)
    {
        this.processor = processor;
        this.schema = schema;
        this.reportProvider = reportProvider;
    }

    private ProcessingReport doValidate(final JsonNode node,
        final boolean deepCheck)
        throws ProcessingException
    {
        final FullData data = new FullData(schema, new SimpleJsonTree(node),
            deepCheck);
        final ProcessingReport report = reportProvider.newReport();
        final ProcessingResult<FullData> result
            =  ProcessingResult.of(processor, report, data);
        return result.getReport();
    }

    private ProcessingReport doValidateUnchecked(final JsonNode node,
        final boolean deepCheck)
    {
        final FullData data = new FullData(schema, new SimpleJsonTree(node),
            deepCheck);
        final ProcessingReport report = reportProvider.newReport();
        final ProcessingResult<FullData> result
            =  ProcessingResult.uncheckedResult(processor, report, data);
        return result.getReport();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessingReport validate(final JsonNode instance,
        final boolean deepCheck)
        throws ProcessingException
    {
        return doValidate(instance, deepCheck);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessingReport validate(final JsonNode instance)
        throws ProcessingException
    {
        return validate(instance, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessingReport validateUnchecked(final JsonNode instance,
        final boolean deepCheck)
    {
        return doValidateUnchecked(instance, deepCheck);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessingReport validateUnchecked(final JsonNode instance)
    {
        return doValidateUnchecked(instance, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validInstance(final JsonNode instance)
        throws ProcessingException
    {
        return doValidate(instance, false).isSuccess();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validInstanceUnchecked(final JsonNode instance)
    {
        return doValidateUnchecked(instance, false).isSuccess();
    }
}
