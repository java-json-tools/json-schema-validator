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

package com.github.fge.jsonschema.keyword.validator.draftv3;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.keyword.validator.helpers.DraftV3TypeKeywordValidator;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.report.ListProcessingReport;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.github.fge.jsonschema.util.NodeType;

import static com.github.fge.jsonschema.messages.KeywordValidationMessages.*;

public final class DisallowKeywordValidator
    extends DraftV3TypeKeywordValidator
{
    public DisallowKeywordValidator(final JsonNode digested)
    {
        super("disallow", digested);
    }

    @Override
    public void validate(
        final Processor<ValidationData, ProcessingReport> processor,
        final ProcessingReport report, final ValidationData data)
        throws ProcessingException
    {
        final JsonNode instance = data.getInstance().getCurrentNode();
        final NodeType type = NodeType.getNodeType(instance);

        if (types.contains(type)) {
            report.error(newMsg(data).msg(DISALLOWED_TYPE)
                .put("disallowed", types).put("found", type));
            return;
        }

        final JsonSchemaTree schemaTree = data.getSchema();
        final ObjectNode fullReport = FACTORY.objectNode();

        JsonPointer ptr;
        ListProcessingReport subReport;
        int nrSuccess = 0;

        for (final int index: schemas) {
            subReport = new ListProcessingReport();
            ptr = basePtr.append(index);
            schemaTree.append(ptr);
            processor.process(subReport, data);
            schemaTree.pop();
            fullReport.put(ptr.toString(), subReport.asJson());
            if (subReport.isSuccess())
                nrSuccess++;
        }

        if (nrSuccess != 0)
            report.error(newMsg(data).msg(DISALLOW_SCHEMA)
                .put("reports", fullReport));
    }
}
