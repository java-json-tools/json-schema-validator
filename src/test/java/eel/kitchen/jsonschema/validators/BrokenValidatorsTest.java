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
import org.testng.annotations.Test;

public class BrokenValidatorsTest
{
    private static final ValidatorFactory factory
        = new ValidatorFactory();

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^there is already a validator for" +
            " type string$"
    )
    public void testDuplicateValidator()
        throws MalformedJasonSchemaException
    {
        factory.registerValidator("string", InstantiationChallengedValidator.class);
    }

    // FIXME!
//    @Test
//    public void testInstantiationProblem()
//        throws MalformedJasonSchemaException, IOException
//    {
//        factory.registerValidator("pwet", InstantiationChallengedValidator.class);
//
//        final ObjectMapper mapper = new ObjectMapper();
//        final String dummySchema = "{ \"type\": \"pwet\" }";
//        final JsonNode schemaNode = mapper.readTree(dummySchema);
//
//        final Validator v
//            = factory.getValidator(schemaNode, mapper.readTree("{}"));
//        Assert.assertNull(v);
//    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^cannot find constructor$"
    )
    public void testNoConstructor()
        throws MalformedJasonSchemaException
    {
        factory.registerValidator("mytype", ConstructorChallengedValidator.class);
    }
}
