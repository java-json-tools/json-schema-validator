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
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.testutils.DataProviderArguments;
import org.eel.kitchen.testutils.JsonDataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public final class MaximumKeywordValidatorTest
{
    @Test(
        dataProviderClass = JsonDataProvider.class,
        dataProvider = "getData"
    )
    @DataProviderArguments(fileName = "/keyword/maximum.json")
    public void testMaximumKeyword(final JsonNode node)
    {
        final JsonNode schemaNode = node.get("schema");
        final JsonNode instance = node.get("data");
        final boolean valid = node.get("valid").booleanValue();

        final ValidationReport report = new ValidationReport();

        final KeywordValidator validator
            = new MaximumKeywordValidator(schemaNode);

        validator.validateInstance(report, instance);

        assertEquals(report.isSuccess(), valid, "instance " + instance
            + " should have validated as " + valid);
    }
}
