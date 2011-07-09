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

package eel.kitchen.jsonschema;

import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import eel.kitchen.jsonschema.validators.Validator;
import eel.kitchen.jsonschema.validators.ValidatorFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class BrokenSchemasTest
{
    private final ValidatorFactory factory = new ValidatorFactory();
    private JsonNode testNode, dummy;
    private Validator v;
    private List<String> ret;
    private final Class<? extends Validator> typeMismatch
        = ValidatorFactory.TypeMismatchValidator.class;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        testNode = JasonLoader.load("broken-schemas.json");
        dummy = new ObjectMapper().readTree("\"hello\"");
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^schema is null$"
    )
    public void testNullSchema()
        throws MalformedJasonSchemaException
    {
        new JasonSchema(null);
    }

    @Test(
        expectedExceptions = MalformedJasonSchemaException.class,
        expectedExceptionsMessageRegExp = "^schema is not a JSON object$"
    )
    public void testNotASchema()
        throws MalformedJasonSchemaException
    {
        new JasonSchema(testNode.get("not-a-schema"));
    }

    @Test
    public void testIllegalType()
        throws MalformedJasonSchemaException
    {
        v = factory.getValidator(testNode.get("illegal-type"), dummy);
        assertEquals(v.getClass(), typeMismatch);

        assertFalse(v.validate(dummy));

        ret = v.getValidationErrors();

        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "schema does not allow any type??");
    }

    @Test
    public void testIllegalTypeArray()
        throws MalformedJasonSchemaException
    {
        v = factory.getValidator(testNode.get("illegal-type-array"), dummy);
        assertEquals(v.getClass(), typeMismatch);

        assertFalse(v.validate(dummy));

        ret = v.getValidationErrors();

        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "schema does not allow any type??");
    }

    @Test
    public void testEmptyTypeSet()
        throws MalformedJasonSchemaException
    {
        v = factory.getValidator(testNode.get("empty-type-set"), dummy);
        assertEquals(v.getClass(), typeMismatch);

        assertFalse(v.validate(dummy));

        ret = v.getValidationErrors();

        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "schema does not allow any type??");
    }

    @Test
    public void testDisallowAny()
        throws MalformedJasonSchemaException
    {
        v = factory.getValidator(testNode.get("disallow-any"), dummy);
        assertEquals(v.getClass(), typeMismatch);

        assertFalse(v.validate(dummy));

        ret = v.getValidationErrors();

        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "schema does not allow any type??");
    }

    @Test
    public void testIntegerVsNumber()
        throws MalformedJasonSchemaException
    {
        v = factory.getValidator(testNode.get("integer-vs-number"), dummy);
        assertEquals(v.getClass(), typeMismatch);

        assertFalse(v.validate(dummy));

        ret = v.getValidationErrors();

        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "schema does not allow any type??");
    }

    @Test
    public void testUnknownType()
        throws MalformedJasonSchemaException
    {
        v = factory.getValidator(testNode.get("unknown-type"), dummy);
        assertEquals(v.getClass(), typeMismatch);

        assertFalse(v.validate(dummy));

        ret = v.getValidationErrors();

        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "schema does not allow any type??");
    }
}
