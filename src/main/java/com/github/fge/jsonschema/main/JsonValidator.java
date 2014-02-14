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
import com.fasterxml.jackson.databind.node.MissingNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.JsonReferenceException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.load.SchemaLoader;
import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.processing.ProcessingResult;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.processors.validation.ValidationProcessor;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.report.ReportProvider;
import com.github.fge.jsonschema.tree.JsonTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.tree.SimpleJsonTree;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;

import javax.annotation.concurrent.Immutable;

/**
 * A generic schema/instance validator
 *
 * <p>One such instance exists per {@link JsonSchemaFactory}. In fact, you have
 * to go through a factory to obtain an instance.</p>
 *
 * <p>This class is also responsible for building {@link JsonSchema} instances.
 * </p>
 *
 * @see JsonSchemaFactory#getValidator()
 */
@Immutable
public final class JsonValidator
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private final SchemaLoader loader;
    private final ValidationProcessor processor;
    private final ReportProvider reportProvider;

    /**
     * Package private (and only) constructor
     *
     * @param loader the schema loader
     * @param processor the validation processor
     * @param reportProvider the report provider
     */
    JsonValidator(final SchemaLoader loader,
        final ValidationProcessor processor,
        final ReportProvider reportProvider)
    {
        this.loader = loader;
        this.processor = processor;
        this.reportProvider = reportProvider;
    }

    /**
     * Validate a schema/instance pair
     *
     * <p>The third boolean argument instructs the validator as to whether it
     * should validate children even if the container (array or object) fails
     * to validate.</p>
     *
     * @param schema the schema
     * @param instance the instance
     * @param deepCheck see description
     * @return a validation report
     * @throws ProcessingException an exception occurred during validation
     * @throws NullPointerException the schema or instance is null
     *
     * @since 2.1.8
     */
    public ProcessingReport validate(final JsonNode schema,
        final JsonNode instance, final boolean deepCheck)
        throws ProcessingException
    {
        final ProcessingReport report = reportProvider.newReport();
        final FullData data = buildData(schema, instance, deepCheck);
        return ProcessingResult.of(processor, report, data).getReport();
    }


    /**
     * Validate a schema/instance pair, "fast" version
     *
     * <p>This calls {@link #validate(JsonNode, JsonNode, boolean)} with {@code
     * false} as the third argument.</p>
     *
     * @param schema the schema
     * @param instance the instance
     * @return a validation report
     * @throws ProcessingException an exception occurred during validation
     * @throws NullPointerException the schema or instance is null
     */
    public ProcessingReport validate(final JsonNode schema,
        final JsonNode instance)
        throws ProcessingException
    {
        return validate(schema, instance, false);
    }

    /**
     * Validate a schema/instance pair (unchecked mode)
     *
     * <p>The third boolean argument instructs the validator as to whether it
     * should validate children even if the container (array or object) fails
     * to validate.</p>
     *
     * <p>The same warnings as described in {@link
     * JsonSchema#validateUnchecked(JsonNode)} apply</p>
     *
     * @param schema the schema
     * @param instance the instance
     * @param deepCheck see description
     * @return a validation report
     * @throws NullPointerException the schema or instance is null
     *
     * @since 2.1.8
     */
    public ProcessingReport validateUnchecked(final JsonNode schema,
        final JsonNode instance, final boolean deepCheck)
    {
        final ProcessingReport report = reportProvider.newReport();
        final FullData data = buildData(schema, instance, deepCheck);
        return ProcessingResult.uncheckedResult(processor, report, data)
            .getReport();
    }

    /**
     * Validate a schema/instance pair (unchecked mode), "fast" version
     *
     * <p>This calls {@link #validateUnchecked(JsonNode, JsonNode, boolean)}
     * with {@code false} as a third argument.</p>
     *
     * <p>The same warnings as described in {@link
     * JsonSchema#validateUnchecked(JsonNode)} apply</p>
     *
     * @param schema the schema
     * @param instance the instance
     * @return a validation report
     * @throws NullPointerException the schema or instance is null
     */
    public ProcessingReport validateUnchecked(final JsonNode schema,
        final JsonNode instance)
    {
        return validateUnchecked(schema, instance, false);
    }

    /**
     * Build a {@link JsonSchema} instance
     *
     * @param schema the schema
     * @param pointer the pointer into the schema
     * @return a new {@link JsonSchema}
     * @throws ProcessingException resolving the pointer against the schema
     * leads to a {@link MissingNode}
     * @throws NullPointerException the schema or pointer is null
     */
    JsonSchema buildJsonSchema(final JsonNode schema, final JsonPointer pointer)
        throws ProcessingException
    {
        final SchemaTree tree = loader.load(schema).setPointer(pointer);
        if (tree.getNode().isMissingNode())
            throw new JsonReferenceException(new ProcessingMessage()
                .setMessage(BUNDLE.getMessage("danglingRef")));
        return new JsonSchema(processor, tree, reportProvider);
    }

    /**
     * Build a {@link JsonSchema} instance
     *
     * @param uri the URI to load the schema from
     * @return a {@link JsonSchema}
     * @throws ProcessingException invalid URI, or URI did not resolve to a
     * JSON Schema
     * @throws NullPointerException URI is null
     */
    JsonSchema buildJsonSchema(final String uri)
        throws ProcessingException
    {
        final JsonRef ref = JsonRef.fromString(uri);
        if (!ref.isLegal())
            throw new JsonReferenceException(new ProcessingMessage()
                .setMessage(BUNDLE.getMessage("illegalJsonRef")));
        final SchemaTree tree
            = loader.get(ref.getLocator()).setPointer(ref.getPointer());
        if (tree.getNode().isMissingNode())
            throw new JsonReferenceException(new ProcessingMessage()
                .setMessage(BUNDLE.getMessage("danglingRef")));
        return new JsonSchema(processor, tree, reportProvider);
    }

    /**
     * Get the raw processor for this validator (package private)
     *
     * @return the processor (a {@link ValidationProcessor}
     */
    Processor<FullData, FullData> getProcessor()
    {
        return processor;
    }

    private FullData buildData(final JsonNode schema, final JsonNode instance,
        final boolean deepCheck)
    {
        BUNDLE.checkNotNull(schema, "nullSchema");
        BUNDLE.checkNotNull(instance, "nullInstance");
        final SchemaTree schemaTree = loader.load(schema);
        final JsonTree tree = new SimpleJsonTree(instance);
        return new FullData(schemaTree, tree, deepCheck);
    }
}
