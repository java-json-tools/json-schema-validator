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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.draftv4;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.JsonValidator;
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.jsonschema.draftv4.newkeywords.PropertiesSyntaxValidator;
import org.eel.kitchen.jsonschema.draftv4.newkeywords.RequiredKeywordValidator;
import org.eel.kitchen.jsonschema.draftv4.newkeywords.RequiredSyntaxValidator;
import org.eel.kitchen.jsonschema.keyword.AlwaysTrueKeywordValidator;
import org.eel.kitchen.util.JsonLoader;
import org.eel.kitchen.util.NodeType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

public final class KeywordRegistrationTest
{
    private JsonNode testNode;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        testNode = JsonLoader.fromResource("/draftv4/example" + ".json");
    }

    @Test
    public void testDraftV3SchemaisInvalid()
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

        assertEquals(report.getMessages().get(0), "#: missing properties"
            + " [p1, p2]");
    }

    private JsonValidator prepareValidator(final String name)
    {
        final JsonNode schema = testNode.get(name);
        final JsonValidator ret = new JsonValidator(schema);
        ret.unregisterValidator("properties");
        ret.unregisterValidator("required");
        ret.registerValidator("properties", PropertiesSyntaxValidator.class,
            AlwaysTrueKeywordValidator.class, NodeType.OBJECT);
        ret.registerValidator("required", RequiredSyntaxValidator.class,
            RequiredKeywordValidator.class, NodeType.OBJECT);

        return ret;
    }
}
