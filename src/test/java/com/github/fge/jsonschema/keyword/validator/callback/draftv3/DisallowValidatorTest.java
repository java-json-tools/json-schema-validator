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

package com.github.fge.jsonschema.keyword.validator.callback.draftv3;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import org.mockito.ArgumentCaptor;

import static com.github.fge.jsonschema.TestUtils.*;
import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.mockito.Mockito.*;

public final class DisallowValidatorTest
    extends DraftV3CallbackValidatorTest
{
    public DisallowValidatorTest()
    {
        super("disallow", JsonPointer.of("disallow", 0),
            JsonPointer.of("disallow", 1));
    }

    @Override
    protected void checkOkOk(final ProcessingReport report)
        throws ProcessingException
    {
        final ArgumentCaptor<ProcessingMessage> captor
            = ArgumentCaptor.forClass(ProcessingMessage.class);

        verify(report).error(captor.capture());

        final ProcessingMessage message = captor.getValue();
        final ObjectNode reports = FACTORY.objectNode();

        final ArrayNode oneReport = FACTORY.arrayNode();
        reports.put(ptr1.toString(), oneReport);
        reports.put(ptr2.toString(), oneReport);

        assertMessage(message)
            .isValidationError(keyword,
                BUNDLE.printf("err.draftv3.disallow.schema", 2, 2))
                .hasField("matched", 2).hasField("nrSchemas", 2)
                .hasField("reports", reports);
    }

    @Override
    protected void checkOkKo(final ProcessingReport report)
        throws ProcessingException
    {
        final ArgumentCaptor<ProcessingMessage> captor
            = ArgumentCaptor.forClass(ProcessingMessage.class);

        verify(report).error(captor.capture());

        final ProcessingMessage message = captor.getValue();
        final ObjectNode reports = FACTORY.objectNode();

        final ArrayNode oneReport = FACTORY.arrayNode();
        oneReport.add(MSG.asJson());
        reports.put(ptr1.toString(), FACTORY.arrayNode());
        reports.put(ptr2.toString(), oneReport);

        assertMessage(message)
            .isValidationError(keyword,
                BUNDLE.printf("err.draftv3.disallow.schema", 1, 2))
            .hasField("matched", 1).hasField("nrSchemas", 2)
            .hasField("reports", reports);
    }

    @Override
    protected void checkKoKo(final ProcessingReport report)
        throws ProcessingException
    {
        verify(report, never()).error(anyMessage());
    }

    @Override
    protected JsonNode generateSchema()
    {
        final ArrayNode schemas = FACTORY.arrayNode();
        schemas.add(sub1);
        schemas.add(sub2);
        final ObjectNode ret = FACTORY.objectNode();
        ret.put(keyword, schemas);
        return ret;
    }

    @Override
    protected JsonNode generateInstance()
    {
        return FACTORY.nullNode();
    }

    @Override
    protected JsonNode generateDigest()
    {
        final ObjectNode ret = FACTORY.objectNode();
        ret.put(keyword, FACTORY.arrayNode());
        final ArrayNode schemas = FACTORY.arrayNode();
        schemas.add(0);
        schemas.add(1);
        ret.put("schemas", schemas);
        return ret;
    }
}
