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

package com.github.fge.jsonschema.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

public final class SelfValidationTest
{
    private static final JsonValidator VALIDATOR
        = JsonSchemaFactory.byDefault().getValidator();

    private final JsonNode draftv3;
    private final JsonNode draftv4;

    public SelfValidationTest()
        throws IOException
    {
        draftv3 = JsonLoader.fromResource("/draftv3/schema");
        draftv4 = JsonLoader.fromResource("/draftv4/schema");
    }

    @Test(invocationCount = 10, threadPoolSize = 4)
    public void v4ValidatesItselfButNotV3()
        throws ProcessingException
    {
        final ProcessingReport r1 = VALIDATOR.validate(draftv4, draftv4);
        final ProcessingReport r2 = VALIDATOR.validate(draftv4, draftv3);

        assertTrue(r1.isSuccess());
        assertFalse(r2.isSuccess());
    }


    @Test(invocationCount = 10, threadPoolSize = 4)
    public void v3ValidatesItself()
        throws ProcessingException
    {
        final ProcessingReport r1 = VALIDATOR.validate(draftv3, draftv3);

        assertTrue(r1.isSuccess());
    }

}
