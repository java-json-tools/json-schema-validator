/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.keyword.validator.draftv3;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.report.ListProcessingReport;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.keyword.validator.helpers.DraftV3TypeKeywordValidator;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;

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
        final ProcessingReport report, final MessageBundle bundle,
        final FullData data)
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
            report.error(newMsg(data, bundle, "err.common.typeNoMatch")
                .putArgument("found", type)
                .putArgument("expected", toArrayNode(types)));

        if (!schemas.isEmpty())
            report.error(newMsg(data, bundle, "err.common.schema.noMatch")
                .putArgument("nrSchemas", schemas.size())
                .put("reports", fullReport));
    }
}
