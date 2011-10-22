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

package eel.kitchen.jsonschema.v2;

import eel.kitchen.util.JasonHelper;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public final class IntegerJsonLeafValidatorTest
{
    private JsonNode invalidSchemas, tests;
    private static final JsonInstance dummy
        = new JsonLeafInstance(JsonNodeFactory.instance.numberNode(3));

    @BeforeClass
    public void setUp()
        throws IOException
    {
        final JsonNode node = JasonHelper.load("v2/integer.json");
        invalidSchemas = node.get("invalid-schemas");
        tests = node.get("tests");
    }


    @Test
    public void testInvalidMinimum()
    {
        testInvalidSchema("invalid-minimum");
    }

    @Test
    public void testInvalidExclusiveMinimum()
    {
        testInvalidSchema("invalid-exclusiveminimum");
    }

    @Test
    public void testMinimumOnly()
    {
        testValidation("minimum-only");
    }

    @Test
    public void testInclusiveMinimum()
    {
        testValidation("inclusive-minimum");
    }

    @Test
    public void testExclusiveMinimum()
    {
        testValidation("exclusive-minimum");
    }

    private void testInvalidSchema(final String name)
    {
        final JsonNode schema = invalidSchemas.get(name);
        final JsonValidator v = new JsonLeafValidator(NodeType.INTEGER, schema);

        assertFalse(v.visit(dummy));
    }

    private void testValidation(final String name)
    {
        final JsonNode node = tests.get(name);
        final JsonNode
            schema = node.get("schema"),
            good = node.get("good"),
            bad = node.get("bad");

        final JsonValidator v = new JsonLeafValidator(NodeType.INTEGER, schema);

        assertTrue(v.visit(new JsonLeafInstance(good)));
        assertFalse(v.visit(new JsonLeafInstance(bad)));
    }
}
