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

public final class SyntaxValidator
{
    private final Processor<SchemaHolder, SchemaHolder> processor;

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

    public boolean schemaIsValid(final JsonNode schema)
    {
        final ProcessingReport report = new DevNullProcessingReport();
        return getResult(schema, report).isSuccess();
    }

    public ProcessingReport validateSchema(final JsonNode schema)
    {
        final ProcessingReport report = new ListProcessingReport();
        return getResult(schema, report).getReport();
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
