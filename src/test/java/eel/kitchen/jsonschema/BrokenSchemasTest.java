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

import eel.kitchen.jsonschema.validators.SchemaValidator;
import eel.kitchen.jsonschema.validators.Validator;
import eel.kitchen.jsonschema.validators.errors.TypeMismatchValidator;
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class BrokenSchemasTest
{
    private static final JsonNode testNode;
    private static final JsonNode dummy;
    private static final ValidatorFactory factory
        = new ValidatorFactory();
    private static final Class<? extends Validator>
        typeMismatch = TypeMismatchValidator.class,
        schemaFailure = SchemaValidator.class;

    static {
        try {
            testNode = JasonHelper.load("broken-schemas.json");
            dummy = new ObjectMapper().readTree("\"hello\"");
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private JasonSchema schema;
    private Validator v;
    private List<String> ret;

    @Test
    public void testNullSchema()
    {
        schema = new JasonSchema(null);
        assertFalse(schema.validate(dummy));

        ret = schema.getMessages();
        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "$: schema is null");
    }

    @Test
    public void testNotASchema()
    {
        schema = new JasonSchema(testNode.get("not-a-schema"));
        assertFalse(schema.validate(dummy));

        ret = schema.getMessages();
        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "$: schema is not an object");
    }

    @Test
    public void testIllegalType()
    {
        v = factory.getValidator(testNode.get("illegal-type"), dummy);
        assertEquals(v.getClass(), schemaFailure);

        assertFalse(v.validate(dummy));

        ret = v.getMessages();

        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "type property is neither a string nor an "
            + "array");
    }

    @Test
    public void testIllegalTypeArray()
    {
        v = factory.getValidator(testNode.get("illegal-type-array"), dummy);
        assertEquals(v.getClass(), schemaFailure);

        assertFalse(v.validate(dummy));

        ret = v.getMessages();

        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "non string element in type property array");
    }

    @Test
    public void testEmptyTypeSet()
    {
        v = factory.getValidator(testNode.get("empty-type-set"), dummy);
        assertEquals(v.getClass(), typeMismatch);

        assertFalse(v.validate(dummy));

        ret = v.getMessages();

        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "schema does not allow any type??");
    }

    @Test
    public void testDisallowAny()
    {
        v = factory.getValidator(testNode.get("disallow-any"), dummy);
        assertEquals(v.getClass(), typeMismatch);

        assertFalse(v.validate(dummy));

        ret = v.getMessages();

        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "schema does not allow any type??");
    }

    @Test
    public void testIntegerVsNumber()
    {
        v = factory.getValidator(testNode.get("integer-vs-number"), dummy);
        assertEquals(v.getClass(), typeMismatch);

        assertFalse(v.validate(dummy));

        ret = v.getMessages();

        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "schema does not allow any type??");
    }

    @Test
    public void testUnknownType()
    {
        v = factory.getValidator(testNode.get("unknown-type"), dummy);
        assertEquals(v.getClass(), schemaFailure);

        assertFalse(v.validate(dummy));

        ret = v.getMessages();

        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "unknown type pwet");
    }

    @Test
    public void testVoidEnum()
    {
        schema = new JasonSchema(testNode.get("void-enum"));

        assertFalse(schema.validate(dummy));

        ret = schema.getMessages();
        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "$: no element in enum has expected type "
            + "string");
    }
}
