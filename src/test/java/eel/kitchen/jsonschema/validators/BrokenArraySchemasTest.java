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
import eel.kitchen.jsonschema.validators.type.ArrayValidator;
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

public class BrokenArraySchemasTest
{
    private JsonNode schemas;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        schemas = JasonHelper.load("broken-array-schemas.json");
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^minItems should be an integer$"
    )
    public void testBrokenMinItems()
        throws MalformedJasonSchemaException
    {
        new ArrayValidator(schemas.get("broken-minItems")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^maxItems should be an integer$"
    )
    public void testBrokenMaxItems()
        throws MalformedJasonSchemaException
    {
        new ArrayValidator(schemas.get("broken-maxItems")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^minItems should be less than or " +
            "equal to maxItems$"
    )
    public void testInvertedMinMax()
        throws MalformedJasonSchemaException
    {
        new ArrayValidator(schemas.get("inverted-minmax")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^uniqueItems should be a boolean$"
    )
    public void testBrokenUniqueItems()
        throws MalformedJasonSchemaException
    {
        new ArrayValidator(schemas.get("broken-uniqueItems")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^items should be an object or an " +
            "array$"
    )
    public void testBrokenItems()
        throws MalformedJasonSchemaException
    {
        new ArrayValidator(schemas.get("broken-items")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^members of the items array should" +
            " be objects$"
    )
    public void testBrokenItemsValue()
        throws MalformedJasonSchemaException
    {
        new ArrayValidator(schemas.get("broken-items-value")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^the items array is empty$"
    )
    public void testEmptyItems()
        throws MalformedJasonSchemaException
    {
        new ArrayValidator(schemas.get("empty-items")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^additionalItems is neither a "
            + "boolean nor an object$"
    )
    public void testBrokenAdditionalItems()
        throws MalformedJasonSchemaException
    {
        new ArrayValidator(schemas.get("broken-additionalItems")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^minItems is greater than what the"
            + " schema allows \\(tuples, additional\\)$"
    )
    public void testIncoherentMinItems()
        throws MalformedJasonSchemaException
    {
        new ArrayValidator(schemas.get("incoherent-minItems")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^maxItems is lower than what the "
            + "schema requires \\(tuples, additional\\)$"
    )
    public void testIncoherentMaxItems()
        throws MalformedJasonSchemaException
    {
        new ArrayValidator(schemas.get("incoherent-maxItems")).setup();
    }
}
