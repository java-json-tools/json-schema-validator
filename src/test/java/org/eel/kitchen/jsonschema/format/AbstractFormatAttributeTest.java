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

package org.eel.kitchen.jsonschema.format;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.eel.kitchen.jsonschema.validator.ValidationContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.annotations.Sets;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.*;

public abstract class AbstractFormatAttributeTest
{
    private final FormatAttribute attribute;
    private final String fmt;

    private final JsonNode testData;

    AbstractFormatAttributeTest(final FormatAttribute attribute,
        final String resourceName)
        throws IOException
    {
        this.attribute = attribute;
        fmt = resourceName;

        testData = JsonLoader.fromResource("/format/" + resourceName + ".json");
    }

    @DataProvider
    protected final Iterator<Object[]> getData()
    {
        final Set<Object[]> set = Sets.newHashSet();

        for (final JsonNode node: testData)
            set.add(new Object[] {
                node.get("data"),
                node.get("valid").booleanValue()
            }
            );

        return set.iterator();
    }

    @Test(dataProvider = "getData", invocationCount = 10, threadPoolSize = 4)
    public final void testSpecifier(final JsonNode data, final boolean valid)
    {
        final ValidationContext ctx = new ValidationContext(null);
        final ValidationReport report = new ValidationReport();

        attribute.checkValue(fmt, report, data);

        assertEquals(report.isSuccess(), valid);
    }
}
