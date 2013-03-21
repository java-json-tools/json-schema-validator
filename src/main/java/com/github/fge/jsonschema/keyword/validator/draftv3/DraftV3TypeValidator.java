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
import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.keyword.validator.helpers.DraftV3TypeKeywordValidator;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.report.ListProcessingReport;
import com.github.fge.jsonschema.report.LogLevel;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;

import static com.github.fge.jsonschema.messages.KeywordValidationMessages.*;

/**
 * Keyword validator for draft v3's {@code type}
 */
public final class DraftV3TypeValidator
    extends DraftV3TypeKeywordValidator
{
    public DraftV3TypeValidator(final JsonNode digest)
    {
        super("type", digest);
    }

    @Override
    public void validate(final Processor<FullData, FullData> processor,
        final ProcessingReport report, final FullData data)
        throws ProcessingException
    {
        final JsonNode instance = data.getInstance().getNode();
        final NodeType type = NodeType.getNodeType(instance);

        /*
         * Check the primitive type first
         */
        final boolean primitiveOK = types.contains(type);

        if (primitiveOK)
            return;

        /*
         * If not OK, check the subschemas
         */
        final ObjectNode fullReport = FACTORY.objectNode();
        final SchemaTree tree = data.getSchema();
        final JsonPointer schemaPointer = tree.getPointer();

        ListProcessingReport subReport;
        JsonPointer ptr;
        FullData newData;
        int nrSuccess = 0;

        for (final int index: schemas) {
            subReport = new ListProcessingReport(report.getLogLevel(),
                LogLevel.FATAL);
            ptr = schemaPointer.append(JsonPointer.of(keyword, index));
            newData = data.withSchema(tree.setPointer(ptr));
            processor.process(subReport, newData);
            fullReport.put(ptr.toString(), subReport.asJson());
            if (subReport.isSuccess())
                nrSuccess++;
        }

        /*
         * If at least one matched, OK
         */
        if (nrSuccess >= 1)
            return;

        /*
         * If no, failure on both counts. We reuse anyOf's message for subschema
         * failure. Also, take care not to output an error if there wasn't any
         * primitive types...
         */
        if (!types.isEmpty())
            report.error(newMsg(data).message(TYPE_NO_MATCH).put("expected", types)
                .put("found", type));

        if (!schemas.isEmpty())
            report.error(newMsg(data).message(ANYOF_FAIL)
                .put("reports", fullReport));
    }
}
