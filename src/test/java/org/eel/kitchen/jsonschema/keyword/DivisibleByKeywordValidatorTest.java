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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.testng.Assert.assertEquals;

public final class DivisibleByKeywordValidatorTest
{
    private static final JsonNodeFactory factory = JsonNodeFactory.instance;

    @DataProvider
    private Object[][] getData()
    {
        return new Object[][] {
            { "1", "3", true },
            { "1.1", "3", false },
            { "9812938091283098", "9812938091283098", true },
            { "9812938091283098.1", "9812938091283098.11", false },
        };
    }

    @Test(dataProvider = "getData")
    public void testDivisibleBy(final String divisor, final String data,
        final boolean valid)
    {
        final JsonNode schemaNode = factory.objectNode()
            .put("divisibleBy", new BigDecimal(divisor));
        final JsonNode instance = factory.numberNode(new BigDecimal(data));

        final ValidationReport report = new ValidationReport();
        final KeywordValidator validator
            = new DivisibleByKeywordValidator(schemaNode);

        validator.validate(report, instance);
        assertEquals(report.isSuccess(), valid, instance + " should have "
            + "validated as " + valid + " using schema " + schemaNode);
    }
}
