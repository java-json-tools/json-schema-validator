/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.validators;

import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import eel.kitchen.jsonschema.validators.type.NumberValidator;
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

public class BrokenNumberSchemasTest
{
    private JsonNode schemas;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        schemas = JasonHelper.load("broken-number-schemas.json");
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^minimum is not a number$"
    )
    public void testBrokenMinimum()
        throws MalformedJasonSchemaException
    {
        new NumberValidator().setSchema(schemas.get("broken-minimum")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^exclusiveMinimum is not a boolean$"
    )
    public void testBrokenExclusiveMinimum()
        throws MalformedJasonSchemaException
    {
        new NumberValidator().setSchema(schemas.get("broken-exclusiveMinimum"))
            .setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^maximum is not a number$"
    )
    public void testBrokenMaximum()
        throws MalformedJasonSchemaException
    {
        new NumberValidator().setSchema(schemas.get("broken-maximum")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^exclusiveMaximum is not a boolean$"
    )
    public void testBrokenExclusiveMaximum()
        throws MalformedJasonSchemaException
    {
        new NumberValidator().setSchema(schemas.get("broken-exclusiveMaximum"))
            .setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^minimum should be less than or " +
            "equal to maximum$"
    )
    public void testInvertedMinMax()
        throws MalformedJasonSchemaException
    {
        new NumberValidator().setSchema(schemas.get("inverted-minmax")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^schema can never validate: " +
            "minimum equals maximum, but one, or both, " +
            "is excluded from matching$"
    )
    public void testImpossibleMatch()
        throws MalformedJasonSchemaException
    {
        new NumberValidator().setSchema(schemas.get("impossible-match")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^divisibleBy is not a number$"
    )
    public void testBrokenDivisor()
        throws MalformedJasonSchemaException
    {
        new NumberValidator().setSchema(schemas.get("broken-divisor")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^divisibleBy cannot be zero$"
    )
    public void testZeroDivisor()
        throws MalformedJasonSchemaException
    {
        new NumberValidator().setSchema(schemas.get("zero-divisor")).setup();
    }
}
