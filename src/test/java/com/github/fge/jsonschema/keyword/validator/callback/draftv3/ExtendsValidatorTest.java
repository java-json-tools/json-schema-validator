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
import com.github.fge.jsonschema.core.report.ProcessingReport;

import static com.github.fge.jsonschema.TestUtils.*;
import static org.mockito.Mockito.*;

public final class ExtendsValidatorTest
    extends DraftV3CallbackValidatorTest
{
    public ExtendsValidatorTest()
    {
        super("extends", JsonPointer.of("extends", 0),
            JsonPointer.of("extends", 1));
    }

    @Override
    protected void checkOkOk(final ProcessingReport report)
        throws ProcessingException
    {
        verify(report, never()).error(anyMessage());
    }

    @Override
    protected void checkOkKo(final ProcessingReport report)
        throws ProcessingException
    {
        verify(report, onlyOnce()).error(anyMessage());
    }

    @Override
    protected void checkKoKo(final ProcessingReport report)
        throws ProcessingException
    {
        verify(report, times(2)).error(anyMessage());
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
        return FACTORY.nullNode();
    }
}
