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
import eel.kitchen.jsonschema.validators.type.ObjectValidator;
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

public class BrokenObjectSchemasTest
{
    private JsonNode schemas;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        schemas = JasonHelper.load("broken-object-schemas.json");
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^properties is not an object$"
    )
    public void testBrokenProperties()
        throws MalformedJasonSchemaException
    {
        new ObjectValidator().setSchema(schemas.get("broken-properties")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^value of a property should be an " +
            "object$"
    )
    public void testBrokenProperty()
        throws MalformedJasonSchemaException
    {
        new ObjectValidator().setSchema(schemas.get("broken-property")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^required should be a boolean$"
    )
    public void testBrokenRequired()
        throws MalformedJasonSchemaException
    {
        new ObjectValidator().setSchema(schemas.get("broken-required")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^additionalProperties is neither a" +
            " boolean nor an object$"
    )
    public void testBrokenAdditional()
        throws MalformedJasonSchemaException
    {
        new ObjectValidator().setSchema(schemas.get("broken-additional")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^dependencies should be an object$"
    )
    public void testBrokenDependencies()
        throws MalformedJasonSchemaException
    {
        new ObjectValidator().setSchema(schemas.get("broken-dependencies")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^dependency value is neither a "
            + "string nor an array$"
    )
    public void testBrokenDependency()
        throws MalformedJasonSchemaException
    {
        new ObjectValidator().setSchema(schemas.get("broken-dependency")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^dependency element is not a "
            + "string$"
    )
    public void testBrokenDependencyInArray()
        throws MalformedJasonSchemaException
    {
        new ObjectValidator().setSchema(schemas.get("broken-dependency-in-array"))
            .setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^duplicate entries in dependencies" +
            " array$"
    )
    public void testDuplicateDependency()
        throws MalformedJasonSchemaException
    {
        new ObjectValidator().setSchema(schemas.get("duplicate-dependency"))
            .setup();
    }
    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^a property cannot depend on " +
            "itself$"
    )
    public void testSelfDependency()
        throws MalformedJasonSchemaException
    {
        new ObjectValidator().setSchema(schemas.get("self-dependency")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^patternProperties should be an " +
            "object$"
    )
    public void testBrokenPatternProperties()
        throws MalformedJasonSchemaException
    {
        new ObjectValidator().setSchema(schemas.get("broken-patternprops")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^invalid regex found in " +
            "patternProperties$"
    )
    public void testPatternPropertiesBrokenRegex()
        throws MalformedJasonSchemaException
    {
        new ObjectValidator().setSchema(schemas.get("patternprops-brokenregex"))
            .setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^values from patternProperties " +
            "should be objects$"
    )
    public void testPatternPropertiesBrokenValue()
        throws MalformedJasonSchemaException
    {
        new ObjectValidator().setSchema(schemas.get("patternprops-brokenvalue"))
            .setup();
    }
}
