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
import com.github.fge.jsonschema.keyword.validator.helpers.SchemaArrayKeywordValidator;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.report.ListProcessingReport;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.JsonSchemaTree;

import static com.github.fge.jsonschema.messages.KeywordValidationMessages.*;

public final class OneOfKeywordValidator
    extends SchemaArrayKeywordValidator
{
    public OneOfKeywordValidator(final JsonNode digest)
    {
        super("oneOf");
    }

    @Override
    public void validate(
        final Processor<ValidationData, ProcessingReport> processor,
        final ProcessingReport report, final ValidationData data)
        throws ProcessingException
    {
        final JsonSchemaTree schemaTree = data.getSchema();
        final JsonNode schemas = schemaTree.getCurrentNode().get(keyword);
        final int size = schemas.size();
        final ObjectNode fullReport = FACTORY.objectNode();

        int nrSuccess = 0;
        ListProcessingReport subReport;
        JsonPointer ptr;

        for (int index = 0; index < size; index++) {
            subReport = new ListProcessingReport();
            ptr = basePointer.append(index);
            schemaTree.append(ptr);
            processor.process(subReport, data);
            schemaTree.pop();
            fullReport.put(ptr.toString(), subReport.asJson());
            if (subReport.isSuccess())
                nrSuccess++;
        }

        if (nrSuccess != 1)
            report.error(newMsg(data).msg(ONEOF_FAIL).put("nrSchemas", size)
                .put("matched", nrSuccess).put("reports", fullReport));
    }
}
