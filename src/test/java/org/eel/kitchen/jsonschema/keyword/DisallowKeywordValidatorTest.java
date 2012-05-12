/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
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

package org.eel.kitchen.jsonschema.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.testutils.DataProviderArguments;
import org.eel.kitchen.testutils.JsonDataProvider;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public final class DisallowKeywordValidatorTest
{
    private static final JsonNodeFactory factory
        = JsonNodeFactory.instance;

    @DataProvider
    private Object[][] getSimpleData()
    {
        return new Object[][] {
            { "array", factory.arrayNode() },
            { "boolean", factory.booleanNode(true) },
            { "number", factory.numberNode(0.1) },
            { "number", factory.numberNode(1) },
            { "null", factory.nullNode() },
            { "integer", factory.numberNode(1) },
            { "object", factory.objectNode() },
            { "string", factory.textNode("") },
        };
    }

    @Test(dataProvider = "getSimpleData")
    public void testSimpleDisallow(final String type, final JsonNode node)
    {
        final JsonNode schemaNode = factory.objectNode().put("disallow", type);

        final KeywordValidator validator
            = new DisallowKeywordValidator(schemaNode);

        final ValidationReport report = new ValidationReport();

        validator.validate(report, node);
        assertFalse(report.isSuccess());
    }

    @Test(
        dataProviderClass = JsonDataProvider.class,
        dataProvider = "getData"
    )
    @DataProviderArguments(fileName = "/keyword/disallow.json")
    public void testDisallow(final JsonNode node)
    {
        final JsonNode schemaNode = node.get("schema");
        final JsonNode data = node.get("data");
        final boolean valid = node.get("valid").booleanValue();

        final ValidationReport report = new ValidationReport();
        final KeywordValidator validator
            = new DisallowKeywordValidator(schemaNode);

        validator.validate(report, data);
        assertEquals(report.isSuccess(), valid, "instance " + data + " "
            + "should have validated as " + valid + " using schema "
            + schemaNode);
    }
}
