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
import com.github.fge.jsonschema.exceptions.JsonReferenceException;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.library.Library;
import com.github.fge.jsonschema.load.Dereferencing;
import com.github.fge.jsonschema.load.SchemaLoader;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ProcessorChain;
import com.github.fge.jsonschema.processing.ProcessorSelector;
import com.github.fge.jsonschema.processors.data.FullValidationContext;
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
import com.google.common.base.Predicate;

import java.util.List;
import java.util.Map;

public final class JsonValidator
{
    private final Dereferencing dereferencing;
    private final SchemaLoader loader;
    private final ValidationProcessor processor;
    private final ReportProvider reportProvider;

    JsonValidator(final JsonSchemaFactory factory)
    {
        dereferencing = factory.loadingConfiguration.getDereferencing();
        loader = new SchemaLoader(factory.loadingConfiguration);
        final RefResolverProcessor refResolver
            = new RefResolverProcessor(loader);

        final ProcessorChain<ValidationData, FullValidationContext> chain
            = ProcessorChain.startWith(refResolver)
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

    private static Processor<ValidationData, FullValidationContext>
        buildProcessor(final JsonSchemaFactory factory)
    {
        final ValidationConfiguration cfg = factory.validationConfiguration;

        ProcessorSelector<ValidationData, FullValidationContext> selector
            = new ProcessorSelector<ValidationData, FullValidationContext>();

        final Map<JsonRef, Library> libraries = cfg.getLibraries();
        final boolean useFormat = cfg.getUseFormat();

        Predicate<ValidationData> predicate;
        ValidationChain chain;

        for (final Map.Entry<JsonRef, Library> entry: libraries.entrySet()) {
            predicate = versionTest(entry.getKey());
            chain = new ValidationChain(entry.getValue(), useFormat);
            selector = selector.when(predicate).then(chain);
        }

        chain = new ValidationChain(cfg.getDefaultLibrary(), useFormat);
        selector = selector.otherwise(chain);

        return selector.getProcessor();
    }

    private static Predicate<ValidationData> versionTest(final JsonRef ref)
    {
        return new Predicate<ValidationData>()
        {
            @Override
            public boolean apply(final ValidationData input)
            {
                return ref.equals(extractDollarSchema(input));
            }
        };
    }

    private static JsonRef extractDollarSchema(final ValidationData data)
    {
        final JsonNode schema = data.getSchema().getBaseNode();
        final JsonNode node = schema.path("$schema");
        if (!node.isTextual())
            return JsonRef.emptyRef();
        try {
            return JsonRef.fromString(node.textValue());
        } catch (JsonReferenceException ignored) {
            return JsonRef.emptyRef();
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

        ret.setExceptionThreshold(LogLevel.FATAL);
        try {
            ret.log(message);
            if (messages != null)
                for (final ProcessingMessage msg: messages)
                    ret.log(msg);
        } catch (ProcessingException ignored) {
        }

        return ret;
    }
}
