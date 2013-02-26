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

package com.github.fge.jsonschema.processors.syntax;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.library.Library;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.processing.ProcessingResult;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ProcessorMap;
import com.github.fge.jsonschema.processors.data.SchemaHolder;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.report.DevNullProcessingReport;
import com.github.fge.jsonschema.report.ListProcessingReport;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
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
public final class SyntaxValidator
{
    private final Processor<SchemaHolder, SchemaHolder> processor;

    /**
     * Constructor
     *
     * @param cfg the validation configuration to use
     */
    public SyntaxValidator(final ValidationConfiguration cfg)
    {
        ProcessorMap<JsonRef, SchemaHolder, SchemaHolder> map = new SchemaMap();

        final SyntaxProcessor byDefault
            = new SyntaxProcessor(cfg.getDefaultLibrary());

        map = map.setDefaultProcessor(byDefault);

        final Map<JsonRef,Library> libraries = cfg.getLibraries();

        JsonRef ref;
        SyntaxProcessor syntaxProcessor;

        for (final Map.Entry<JsonRef, Library> entry: libraries.entrySet()) {
            ref = entry.getKey();
            syntaxProcessor = new SyntaxProcessor(entry.getValue());
            map = map.addEntry(ref, syntaxProcessor);
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
    public Processor<SchemaHolder, SchemaHolder> getProcessor()
    {
        return processor;
    }

    private ProcessingResult<SchemaHolder> getResult(final JsonNode schema,
        final ProcessingReport report)
    {
        final SchemaHolder holder = holder(schema);
        return ProcessingResult.uncheckedResult(processor, report, holder);
    }

    private static SchemaHolder holder(final JsonNode node)
    {
        return new SchemaHolder(new CanonicalSchemaTree(node));
    }

    private static final class SchemaMap
        extends ProcessorMap<JsonRef, SchemaHolder, SchemaHolder>
    {
        @Override
        protected Function<SchemaHolder, JsonRef> f()
        {
            return new Function<SchemaHolder, JsonRef>()
            {
                @Override
                public JsonRef apply(final SchemaHolder input)
                {
                    return input.getValue().getDollarSchema();
                }
            };
        }
    }
}
