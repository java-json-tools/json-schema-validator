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
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.library.Library;
import com.github.fge.jsonschema.load.Dereferencing;
import com.github.fge.jsonschema.load.SchemaLoader;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ProcessorMap;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.processors.data.SchemaContext;
import com.github.fge.jsonschema.processors.data.ValidatorList;
import com.github.fge.jsonschema.processors.ref.RefResolver;
import com.github.fge.jsonschema.processors.validation.ValidationChain;
import com.github.fge.jsonschema.processors.validation.ValidationProcessor;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.report.ListProcessingReport;
import com.github.fge.jsonschema.report.LogLevel;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.report.ReportProvider;
import com.github.fge.jsonschema.tree.JsonTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.tree.SimpleJsonTree;
import com.google.common.base.Function;

import java.util.Map;

public final class JsonValidator
{
    private final Dereferencing dereferencing;
    private final ValidationProcessor processor;
    private final ReportProvider reportProvider;

    JsonValidator(final JsonSchemaFactory factory)
    {
        dereferencing = factory.loadingConfiguration.getDereferencing();
        processor = new ValidationProcessor(buildProcessor(factory));
        reportProvider = factory.reportProvider;
    }

    private void doValidate(final JsonNode schema, final JsonNode instance,
        final ProcessingReport report)
        throws ProcessingException
    {
        final SchemaTree schemaTree = dereferencing.newTree(schema);
        final JsonTree tree = new SimpleJsonTree(instance);
        final FullData data = new FullData(schemaTree, tree);
        processor.process(report, data);
    }

    public ProcessingReport validate(final JsonNode schema,
        final JsonNode instance)
        throws ProcessingException
    {
        final ProcessingReport report = reportProvider.newReport();
        doValidate(schema, instance, report);
        return report;
    }

    public ProcessingReport validateUnchecked(final JsonNode schema,
        final JsonNode instance)
    {
        final ProcessingReport report = reportProvider.newReport();
        try {
            doValidate(schema, instance, report);
            return report;
        } catch (ProcessingException e) {
            return buildUncheckedReport(report, e);
        }
    }

    private ProcessingReport buildUncheckedReport
        (final ProcessingReport report, final ProcessingException e)
    {
        final ProcessingReport ret
            = reportProvider.newReport(report.getLogLevel(), LogLevel.NONE);
        final ProcessingMessage message = e.getProcessingMessage()
            .setLogLevel(LogLevel.FATAL)
            .put("info", "other messages follow (if any)");
        final ListProcessingReport r
            = new ListProcessingReport(report.getLogLevel(), LogLevel.NONE);
        r.log(LogLevel.FATAL, message);
        try {
            r.mergeWith(report);
            ret.mergeWith(r);
        } catch (ProcessingException ignored) {
        }

        return ret;
    }

    private static Processor<SchemaContext, ValidatorList>
        buildProcessor(final JsonSchemaFactory factory)
    {
        final SchemaLoader loader
            = new SchemaLoader(factory.loadingConfiguration);
        final RefResolver resolver = new RefResolver(loader);
        final ValidationConfiguration cfg = factory.validationConfiguration;
        final boolean useFormat = cfg.getUseFormat();

        final Map<JsonRef, Library> libraries = cfg.getLibraries();
        final ValidationChain defaultChain
            = new ValidationChain(resolver, cfg.getDefaultLibrary(), useFormat);
        ProcessorMap<JsonRef, SchemaContext, ValidatorList> map
            = new FullChain().setDefaultProcessor(defaultChain);

        JsonRef ref;
        ValidationChain chain;

        for (final Map.Entry<JsonRef, Library> entry: libraries.entrySet()) {
            ref = entry.getKey();
            chain = new ValidationChain(resolver, entry.getValue(), useFormat);
            map = map.addEntry(ref, chain);
        }

        return map.getProcessor();
    }

    private static final class FullChain
        extends ProcessorMap<JsonRef, SchemaContext, ValidatorList>
    {
        @Override
        protected Function<SchemaContext, JsonRef> f()
        {
            return new Function<SchemaContext, JsonRef>()
            {
                @Override
                public JsonRef apply(final SchemaContext input)
                {
                    return input.getSchema().getDollarSchema();
                }
            };
        }
    }
}
