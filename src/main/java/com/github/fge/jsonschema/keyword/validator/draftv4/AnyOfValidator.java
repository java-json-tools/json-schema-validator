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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.keyword.validator.helpers.SchemaArrayValidator;
import com.github.fge.jsonschema.processing.LogLevel;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.report.ListProcessingReport;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;

import static com.github.fge.jsonschema.messages.KeywordValidationMessages.*;

public final class AnyOfValidator
    extends SchemaArrayValidator
{
    public AnyOfValidator(final JsonNode digest)
    {
        super("anyOf");
    }

    @Override
    public void validate(
        final Processor<ValidationData, ProcessingReport> processor,
        final ProcessingReport report, final ValidationData data)
        throws ProcessingException
    {
        final SchemaTree tree = data.getSchema();
        final JsonNode schemas = tree.getNode().get(keyword);
        final int size = schemas.size();
        final ObjectNode fullReport = FACTORY.objectNode();

        int nrSuccess = 0;
        ListProcessingReport subReport;
        JsonPointer ptr;
        ValidationData newData;

        for (int index = 0; index < size; index++) {
            subReport = new ListProcessingReport(report);
            subReport.setExceptionThreshold(LogLevel.FATAL);
            ptr = basePointer.append(index);
            newData = data.withSchema(tree.append(ptr));
            processor.process(subReport, newData);
            fullReport.put(ptr.toString(), subReport.asJson());
            if (subReport.isSuccess())
                nrSuccess++;
        }

        if (nrSuccess == 0)
            report.error(newMsg(data).msg(ANYOF_FAIL)
                .put("reports", fullReport));
    }
}
