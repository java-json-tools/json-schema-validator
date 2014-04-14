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
 * Keyword validator for draft v3's {@code disallow}
 */
public final class DisallowKeywordValidator
    extends DraftV3TypeKeywordValidator
{
    public DisallowKeywordValidator(final JsonNode digested)
    {
        super("disallow", digested);
    }

    @Override
    public void validate(final Processor<FullData, FullData> processor,
        final ProcessingReport report, final MessageBundle bundle,
        final FullData data)
        throws ProcessingException
    {
        final JsonNode instance = data.getInstance().getNode();
        final NodeType type = NodeType.getNodeType(instance);

        if (types.contains(type)) {
            report.error(newMsg(data, bundle, "err.draftv3.disallow.type")
                .putArgument("found", type)
                .putArgument("disallowed", toArrayNode(types)));
            return;
        }

        final SchemaTree tree = data.getSchema();
        final JsonPointer schemaPointer = tree.getPointer();
        final ObjectNode fullReport = FACTORY.objectNode();

        JsonPointer ptr;
        ListProcessingReport subReport;
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

        if (nrSuccess != 0)
            report.error(newMsg(data, bundle, "err.draftv3.disallow.schema")
                .putArgument("matched", nrSuccess)
                .putArgument("nrSchemas", schemas.size())
                .put("reports", fullReport));
    }
}
