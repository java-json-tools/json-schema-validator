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

package com.github.fge.jsonschema.keyword.validator.draftv4;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.keyword.validator.AbstractKeywordValidator;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processors.data.ValidationData;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.report.ListProcessingReport;
import com.github.fge.jsonschema.report.LogLevel;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;

import static com.github.fge.jsonschema.messages.KeywordValidationMessages.*;

public final class NotValidator
    extends AbstractKeywordValidator
{
    private static final JsonPointer PTR = JsonPointer.empty().append("not");

    public NotValidator(final JsonNode digest)
    {
        super("not");
    }

    @Override
    public void validate(
        final Processor<ValidationData, ProcessingReport> processor,
        final ProcessingReport report, final ValidationData data)
        throws ProcessingException
    {
        final SchemaTree tree = data.getSchema();
        final ProcessingReport subReport = new ListProcessingReport(report);
        subReport.setExceptionThreshold(LogLevel.FATAL);

        processor.process(subReport, data.withSchema(tree.append(PTR)));

        if (subReport.isSuccess())
            report.error(newMsg(data).message(NOT_FAIL));
    }

    @Override
    public String toString()
    {
        return "must not match subschema";
    }
}
