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
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.report.ReportProvider;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.tree.SimpleJsonTree;

public final class JsonSchema
{
    private final ValidationProcessor processor;
    private final SchemaTree schema;
    private final ReportProvider reportProvider;

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

    public ProcessingReport validate(final JsonNode instance)
        throws ProcessingException
    {
        return doValidate(instance);
    }

    public ProcessingReport validateUnchecked(final JsonNode instance)
    {
        return doValidateUnchecked(instance);
    }

    public boolean validInstance(final JsonNode instance)
        throws ProcessingException
    {
        return doValidate(instance).isSuccess();
    }

    public boolean validInstanceUnchecked(final JsonNode instance)
    {
        return doValidateUnchecked(instance).isSuccess();
    }
}
