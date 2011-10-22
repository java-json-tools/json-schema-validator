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

package eel.kitchen.jsonschema.v2.validator;

import eel.kitchen.jsonschema.v2.instance.JsonInstance;
import eel.kitchen.jsonschema.v2.instance.JsonLeafInstance;
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public final class JsonValidatorFactoryTest
{
    private static final JsonValidatorFactory factory
        = JsonValidatorFactory.getInstance();

    private static final JsonInstance dummy = new JsonLeafInstance(
        JsonNodeFactory.instance.objectNode()
    );

    private JsonNode schemas;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        schemas = JasonHelper.load("v2/json-validator-factory.json");
    }

    @Test
    public void testNullSchema()
    {
        testSchemaError("nullschema");
    }

    @Test
    public void testNotASchema()
    {
        testSchemaError("not-a-schema");
    }

    @Test
    public void testRef()
    {
        testSchemaError("$ref");
    }

    @Test
    public void testExtends()
    {
        testSchemaError("extends");
    }

    @Test
    public void testNoType()
    {
        testSchemaError("no-type");
    }

    @Test
    public void testComplexType()
    {
        testSchemaError("complex-type");
    }

    @Test
    public void testUnknownType()
    {
        testSchemaError("unknown-type");
    }

    @Test
    public void testWorkingSchemaAtLast()
    {
        final JsonNodeFactory factory = JsonNodeFactory.instance;
        final ObjectNode schema = factory.objectNode();
        schema.put("type", factory.textNode("object"));

        final JsonValidator v = JsonValidatorFactory.getInstance()
            .getValidator(schema);
        assertTrue(v.validate(dummy));
    }
    private void testSchemaError(final String name)
    {
        final JsonNode node = schemas.get(name);
        final JsonNode schema = node.get("schema");
        final String errmsg = node.get("errmsg").getTextValue();

        final JsonValidator v = factory.getValidator(schema);

        assertFalse(v.validate(dummy));

        final List<String> messages = v.getMessages();

        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), errmsg);
    }
}
