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
import com.github.fge.jsonschema.exceptions.JsonReferenceException;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.load.SchemaLoader;
import com.github.fge.jsonschema.processing.ProcessingResult;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.processors.validation.ValidationProcessor;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.report.ReportProvider;
import com.github.fge.jsonschema.tree.JsonTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.tree.SimpleJsonTree;

import static com.github.fge.jsonschema.messages.RefProcessingMessages.*;

public final class JsonValidator
{
    private final SchemaLoader loader;
    private final ValidationProcessor processor;
    private final ReportProvider reportProvider;

    JsonValidator(final SchemaLoader loader,
        final ValidationProcessor processor,
        final ReportProvider reportProvider)
    {
        this.loader = loader;
        this.processor = processor;
        this.reportProvider = reportProvider;
    }

    public ProcessingReport validate(final JsonNode schema,
        final JsonNode instance)
        throws ProcessingException
    {
        final ProcessingReport report = reportProvider.newReport();
        final FullData data = buildData(schema, instance);
        return ProcessingResult.of(processor, report, data).getReport();
    }

    public ProcessingReport validateUnchecked(final JsonNode schema,
        final JsonNode instance)
    {
        final ProcessingReport report = reportProvider.newReport();
        final FullData data = buildData(schema, instance);
        return ProcessingResult.uncheckedResult(processor, report, data)
            .getReport();
    }

    JsonSchema buildJsonSchema(final JsonNode schema, final JsonPointer pointer)
        throws ProcessingException
    {
        final SchemaTree tree = loader.load(schema).setPointer(pointer);
        if (tree.getNode().isMissingNode())
            throw new JsonReferenceException(new ProcessingMessage()
                .message(DANGLING_REF));
        return new JsonSchema(processor, tree, reportProvider);
    }

    JsonSchema buildJsonSchema(final String uri)
        throws ProcessingException
    {
        final JsonRef ref = JsonRef.fromString(uri);
        if (!ref.isLegal())
            throw new JsonReferenceException(new ProcessingMessage()
                .message(ILLEGAL_JSON_REF));
        final SchemaTree tree
            = loader.get(ref.getLocator()).setPointer(ref.getPointer());
        if (tree.getNode().isMissingNode())
            throw new JsonReferenceException(new ProcessingMessage()
                .message(DANGLING_REF));
        return new JsonSchema(processor, tree, reportProvider);
    }

    private FullData buildData(final JsonNode schema, final JsonNode instance)
    {
        final SchemaTree schemaTree = loader.load(schema);
        final JsonTree tree = new SimpleJsonTree(instance);
        return new FullData(schemaTree, tree);
    }
}
