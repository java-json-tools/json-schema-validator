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

package com.github.fge.jsonschema.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.processing.ProcessingResult;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.processors.validation.ValidationProcessor;
import com.github.fge.jsonschema.report.ListProcessingReport;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.report.ReportProvider;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.tree.SimpleJsonTree;
import net.jcip.annotations.Immutable;

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
public final class JsonSchema
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
    JsonSchema(final ValidationProcessor processor, final SchemaTree schema,
        final ReportProvider reportProvider)
    {
        this.processor = processor;
        this.schema = schema;
        this.reportProvider = reportProvider;
    }

    private ProcessingReport doValidate(final JsonNode node)
        throws ProcessingException
    {
        final FullData data = new FullData(schema, new SimpleJsonTree(node));
        final ProcessingReport report = reportProvider.newReport();
        final ProcessingResult<FullData> result
            =  ProcessingResult.of(processor, report, data);
        return result.getReport();
    }

    private ProcessingReport doValidateUnchecked(final JsonNode node)
    {
        final FullData data = new FullData(schema, new SimpleJsonTree(node));
        final ProcessingReport report = reportProvider.newReport();
        final ProcessingResult<FullData> result
            =  ProcessingResult.uncheckedResult(processor, report, data);
        return result.getReport();
    }

    /**
     * Validate an instance and return a processing report
     *
     * @param instance the instance to validate
     * @return a processing report
     * @throws ProcessingException a processing error occurred during validation
     */
    public ProcessingReport validate(final JsonNode instance)
        throws ProcessingException
    {
        return doValidate(instance);
    }

    /**
     * Validate an instance and return a processing report (unchecked version)
     *
     * <p>Unchecked validation means that conditions which would normally cause
     * the processing to stop with an exception are instead inserted into the
     * resulting report.</p>
     *
     * <p><b>Warning</b>: this means that anomalous events like an unresolvable
     * JSON Reference, or an invalid schema, are <b>masked</b>!</p>
     *
     * @param instance the instance to validate
     * @return a report (a {@link ListProcessingReport} if an exception was
     * thrown during processing)
     */
    public ProcessingReport validateUnchecked(final JsonNode instance)
    {
        return doValidateUnchecked(instance);
    }

    /**
     * Check whether an instance is valid against this schema
     *
     * @param instance the instance
     * @return true if the instance is valid
     * @throws ProcessingException an error occurred during processing
     */
    public boolean validInstance(final JsonNode instance)
        throws ProcessingException
    {
        return doValidate(instance).isSuccess();
    }

    /**
     * Check whether an instance is valid against this schema (unchecked
     * version)
     *
     * <p>The same warnings apply as described in {@link
     * #validateUnchecked(JsonNode)}.</p>
     *
     * @param instance the instance to validate
     * @return true if the instance is valid
     */
    public boolean validInstanceUnchecked(final JsonNode instance)
    {
        return doValidateUnchecked(instance).isSuccess();
    }
}
