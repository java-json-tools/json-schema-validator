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
import eel.kitchen.jsonschema.validators.type.StringValidator;
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

public class BrokenStringSchemasTest
{
    private JsonNode schemas;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        schemas = JasonHelper.load("broken-string-schemas.json");
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^minLength should be an integer$"
    )
    public void testBrokenMinLength()
        throws MalformedJasonSchemaException
    {
        new StringValidator().setSchema(schemas.get("broken-minLength")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^minLength should be greater than " +
            "or equal to 0$"
    )
    public void testNegativeMinLength()
        throws MalformedJasonSchemaException
    {
        new StringValidator().setSchema(schemas.get("negative-minLength")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^maxLength should be an integer$"
    )
    public void testBrokenMaxLength()
        throws MalformedJasonSchemaException
    {
        new StringValidator().setSchema(schemas.get("broken-maxLength")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^maxLength should be greater than " +
            "or equal to 0$"
    )
    public void testNegativeMaxLength()
        throws MalformedJasonSchemaException
    {
        new StringValidator().setSchema(schemas.get("negative-maxLength")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^maxLength should be greater than " +
            "or equal to minLength$"
    )
    public void testInvertedMinMax()
        throws MalformedJasonSchemaException
    {
        new StringValidator().setSchema(schemas.get("inverted-minmax")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^pattern should be a string$"
    )
    public void testBrokenPatternType()
        throws MalformedJasonSchemaException
    {
        new StringValidator().setSchema(schemas.get("broken-pattern-type"))
            .setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^pattern is an invalid regular " +
            "expression"
    )
    public void testIllegalPattern()
        throws MalformedJasonSchemaException
    {
        new StringValidator().setSchema(schemas.get("illegal-pattern")).setup();
    }
}
