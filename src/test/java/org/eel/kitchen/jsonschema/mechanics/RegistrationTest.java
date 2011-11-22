/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.mechanics;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.eel.kitchen.jsonschema.bundle.CommonValidatorBundle;
import org.eel.kitchen.jsonschema.bundle.ValidatorBundle;
import org.eel.kitchen.jsonschema.keyword.draftv4.RequiredKeywordValidator;
import org.eel.kitchen.jsonschema.syntax.draftv4.PropertiesSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.draftv4.RequiredSyntaxValidator;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.JsonValidator;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.util.JsonLoader;
import org.eel.kitchen.util.NodeType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

public final class RegistrationTest
{
    private static final JsonNodeFactory factory = JsonNodeFactory.instance;
    private JsonNode testNode;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        testNode = JsonLoader.fromResource("/draftv4/example" + ".json");
    }

    @Test
    public void testDraftV3SchemaisInvalid()
        throws JsonValidationFailureException
    {
        final JsonValidator validator = prepareValidator("badschema");
        final ValidationReport report
            = validator.validate(testNode.get("good"));

        assertFalse(report.isSuccess());

        assertEquals(report.getMessages().size(), 1);

        assertEquals(report.getMessages().get(0), "#/p1 [schema:required]: "
            + "field has wrong type boolean, expected one of [array]");
    }

    @Test
    public void testValidationWithNewKeywords()
        throws JsonValidationFailureException
    {
        final JsonValidator validator = prepareValidator("schema");

        JsonNode instance;
        ValidationReport report;

        instance = testNode.get("good");
        report = validator.validate(instance);

        assertTrue(report.isSuccess());

        instance = testNode.get("bad");
        report = validator.validate(instance);

        assertFalse(report.isSuccess());

        assertEquals(report.getMessages().size(), 1);

        assertEquals(report.getMessages().get(0), "#: required properties"
            + " [p1, p2] are missing");
    }

    @Test
    public void testNullKeywordRegistration()
        throws JsonValidationFailureException
    {
        final JsonValidator validator = new JsonValidator(factory.objectNode());

        try {
            validator.registerValidator(null, null, null);
            fail("No exception thrown");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "keyword is null");
        }
    }

    @Test
    public void testExistingKeywordRegistrationFailure()
        throws JsonValidationFailureException
    {
        final JsonValidator validator = new JsonValidator(factory.objectNode());

        try {
            validator.registerValidator("default", null, null);
            fail("No exception thrown");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "keyword already registered");
        }
    }

    @Test
    public void testEmptyTypeSetFails()
        throws JsonValidationFailureException
    {
        final JsonValidator validator = new JsonValidator(factory.objectNode());

        try {
            validator.registerValidator("foo", null,
                new RequiredKeywordValidator());
            fail("No exception thrown");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "cannot register a new keyword with "
                + "no JSON type to match against");
        }
    }

    @Test
    public void testUnregisteringNullKeywordFails()
        throws JsonValidationFailureException
    {
        final JsonValidator validator = new JsonValidator(factory.objectNode());

        try {
            validator.unregisterValidator(null);
            fail("No exception thrown");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "keyword is null");
        }
    }

    @Test
    public void testMisbalancedBundle()
    {
        /* Yeah, ugly... */
        final ValidatorBundle myBundle = new CommonValidatorBundle()
        {
        };

        try {
            myBundle.validate();
            fail("No exception thrown");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "registered syntax/keyword validators "
                + "do not cover the same set of keywords");
        }
    }

    private JsonValidator prepareValidator(final String name)
        throws JsonValidationFailureException
    {
        final JsonNode schema = testNode.get(name);
        final JsonValidator ret = new JsonValidator(schema);
        ret.unregisterValidator("properties");
        ret.unregisterValidator("required");
        ret.registerValidator("properties", new PropertiesSyntaxValidator(),
            null, NodeType.OBJECT);
        ret.registerValidator("required", new RequiredSyntaxValidator(),
            new RequiredKeywordValidator(), NodeType.OBJECT);

        return ret;
    }
}
