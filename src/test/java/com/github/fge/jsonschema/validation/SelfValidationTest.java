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

package com.github.fge.jsonschema.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.JsonLoader;
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
