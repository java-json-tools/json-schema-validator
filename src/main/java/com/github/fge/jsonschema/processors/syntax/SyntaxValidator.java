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

package com.github.fge.jsonschema.processors.syntax;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.core.keyword.syntax.SyntaxProcessor;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.core.processing.ProcessingResult;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.processing.ProcessorMap;
import com.github.fge.jsonschema.core.ref.JsonRef;
import com.github.fge.jsonschema.core.report.DevNullProcessingReport;
import com.github.fge.jsonschema.core.report.ListProcessingReport;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.core.tree.key.SchemaKey;
import com.github.fge.jsonschema.core.util.Dictionary;
import com.github.fge.jsonschema.core.util.ValueHolder;
import com.github.fge.jsonschema.library.Library;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.google.common.base.Function;

import java.util.Map;

/**
 * Standalone syntax validator
 *
 * <p>This is the syntax validator built, and returned, by {@link
 * JsonSchemaFactory#getSyntaxValidator()}. It can be used to validate schemas
 * independently of the validation chain. Among other features, it detects
 * {@code $schema} and acts accordingly.</p>
 *
 * <p>Note that the reports used are always {@link ListProcessingReport}s.</p>
 */
// TODO REMOVE
public final class SyntaxValidator
{
    private static final Function<ValueHolder<SchemaTree>, JsonRef> FUNCTION
        = new Function<ValueHolder<SchemaTree>, JsonRef>()
    {
        @Override
        public JsonRef apply(final ValueHolder<SchemaTree> input)
        {
            return input.getValue().getDollarSchema();
        }
    };

    private final Processor<ValueHolder<SchemaTree>, ValueHolder<SchemaTree>>
        processor;

    /**
     * Constructor
     *
     * @param cfg the validation configuration to use
     */
    public SyntaxValidator(final ValidationConfiguration cfg)
    {
        final MessageBundle syntaxMessages = cfg.getSyntaxMessages();
        final ProcessorMap<JsonRef, ValueHolder<SchemaTree>, ValueHolder<SchemaTree>>
            map = new ProcessorMap<JsonRef, ValueHolder<SchemaTree>, ValueHolder<SchemaTree>>(FUNCTION);

        Dictionary<SyntaxChecker> dict;
        dict = cfg.getDefaultLibrary().getSyntaxCheckers();

        final SyntaxProcessor byDefault = new SyntaxProcessor(
            cfg.getSyntaxMessages(), dict);

        map.setDefaultProcessor(byDefault);

        final Map<JsonRef,Library> libraries = cfg.getLibraries();

        JsonRef ref;
        SyntaxProcessor syntaxProcessor;

        for (final Map.Entry<JsonRef, Library> entry: libraries.entrySet()) {
            ref = entry.getKey();
            dict = entry.getValue().getSyntaxCheckers();
            syntaxProcessor = new SyntaxProcessor(syntaxMessages, dict);
            map.addEntry(ref, syntaxProcessor);
        }

        processor = map.getProcessor();
    }

    /**
     * Tell whether a schema is valid
     *
     * @param schema the schema
     * @return true if the schema is valid
     */
    public boolean schemaIsValid(final JsonNode schema)
    {
        final ProcessingReport report = new DevNullProcessingReport();
        return getResult(schema, report).isSuccess();
    }

    /**
     * Validate a schema and return a report
     *
     * @param schema the schema
     * @return a report
     */
    public ProcessingReport validateSchema(final JsonNode schema)
    {
        final ProcessingReport report = new ListProcessingReport();
        return getResult(schema, report).getReport();
    }

    /**
     * Return the underlying processor
     *
     * <p>You can use this processor to chain it with your own.</p>
     *
     * @return a processor performing full syntax validation
     */
    public Processor<ValueHolder<SchemaTree>, ValueHolder<SchemaTree>> getProcessor()
    {
        return processor;
    }

    private ProcessingResult<ValueHolder<SchemaTree>> getResult(final JsonNode schema,
        final ProcessingReport report)
    {
        final ValueHolder<SchemaTree> holder = holder(schema);
        return ProcessingResult.uncheckedResult(processor, report, holder);
    }

    private static ValueHolder<SchemaTree> holder(final JsonNode node)
    {
        return ValueHolder.<SchemaTree>hold("schema",
            new CanonicalSchemaTree(SchemaKey.anonymousKey(), node));
    }
}
