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
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

public class BrokenEnumSchemasTest
{
    private JsonNode schemas;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        schemas = JasonHelper.load("broken-enum-schemas.json");
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^enum is not an array$"
    )
    public void testIllegalEnum()
        throws MalformedJasonSchemaException
    {
        new EnumValidator(schemas.get("illegal-enum")).setup();
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^enum has duplicate values$"
    )
    public void testEnumDuplicates()
        throws MalformedJasonSchemaException
    {
        new EnumValidator(schemas.get("enum-duplicates")).setup();
    }
}
