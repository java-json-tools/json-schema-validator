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
import com.github.fge.jsonschema.processing.ProcessorChain;
import com.github.fge.jsonschema.processing.ProcessorMap;
import com.github.fge.jsonschema.processors.data.FullValidationContext;
import com.github.fge.jsonschema.processors.data.SchemaHolder;
import com.github.fge.jsonschema.processors.data.ValidationContext;
import com.github.fge.jsonschema.processors.data.ValidationData;
import com.github.fge.jsonschema.processors.ref.RefResolverProcessor;
import com.github.fge.jsonschema.processors.validation.ValidationChain;
import com.github.fge.jsonschema.processors.validation.ValidationProcessor;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.report.LogLevel;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.report.ReportProvider;
import com.github.fge.jsonschema.tree.JsonTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.tree.SimpleJsonTree;
import com.github.fge.jsonschema.util.NodeType;
import com.google.common.base.Function;

import java.util.List;
import java.util.Map;

public final class JsonValidator
{
    private final Dereferencing dereferencing;
    private final ValidationProcessor processor;
    private final ReportProvider reportProvider;

    JsonValidator(final JsonSchemaFactory factory)
    {
        dereferencing = factory.loadingConfiguration.getDereferencing();
        final SchemaLoader loader
            = new SchemaLoader(factory.loadingConfiguration);
        final RefResolverProcessor refResolver
            = new RefResolverProcessor(loader);

        final ProcessorChain<ValidationContext, FullValidationContext> chain
            = ProcessorChain.startWith(new RefWrapper(refResolver))
                .chainWith(buildProcessor(factory));

        processor = new ValidationProcessor(chain.getProcessor());

        reportProvider = factory.reportProvider;
    }

    private void doValidate(final JsonNode schema, final JsonNode instance,
        final ProcessingReport report)
        throws ProcessingException
    {
        final SchemaTree schemaTree = dereferencing.newTree(schema);
        final JsonTree tree = new SimpleJsonTree(instance);
        final ValidationData data = new ValidationData(schemaTree, tree);
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
        final ProcessingReport ret = reportProvider.newReport();
        final List<ProcessingMessage> messages = report.getMessages();
        final ProcessingMessage message = e.getProcessingMessage()
            .setLogLevel(LogLevel.FATAL)
            .put("info", "other messages follow (if any)");

        ret.setExceptionThreshold(LogLevel.NONE);
        try {
            ret.log(message);
            if (messages != null)
                for (final ProcessingMessage msg: messages)
                    ret.log(msg);
        } catch (ProcessingException ignored) {
        }

        return ret;
    }

    private static Processor<ValidationContext, FullValidationContext>
        buildProcessor(final JsonSchemaFactory factory)
    {
        final ValidationConfiguration cfg = factory.validationConfiguration;

        final Map<JsonRef, Library> libraries = cfg.getLibraries();
        final ValidationChain defaultChain
            = new ValidationChain(cfg.getDefaultLibrary());
        ProcessorMap<JsonRef, ValidationContext, FullValidationContext> map
            = new FullChain().setDefaultProcessor(defaultChain);

        JsonRef ref;
        ValidationChain chain;

        for (final Map.Entry<JsonRef, Library> entry: libraries.entrySet()) {
            ref = entry.getKey();
            chain = new ValidationChain(entry.getValue());
            map = map.addEntry(ref, chain);
        }

        return map.getProcessor();
    }

    private static final class FullChain
        extends ProcessorMap<JsonRef, ValidationContext, FullValidationContext>
    {
        @Override
        protected Function<ValidationContext, JsonRef> f()
        {
            return new Function<ValidationContext, JsonRef>()
            {
                @Override
                public JsonRef apply(final ValidationContext input)
                {
                    return input.getSchema().getDollarSchema();
                }
            };
        }
    }

    private static final class RefWrapper
        implements Processor<ValidationContext, ValidationContext>
    {
        private final RefResolverProcessor processor;

        private RefWrapper(final RefResolverProcessor processor)
        {
            this.processor = processor;
        }

        @Override
        public ValidationContext process(final ProcessingReport report,
            final ValidationContext input)
            throws ProcessingException
        {
            final NodeType type = input.getInstanceType();
            final SchemaHolder in = new SchemaHolder(input.getSchema());
            final SchemaHolder out = processor.process(report, in);
            return new ValidationContext(out.getValue(), type);
        }
    }
}
