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
import org.eel.kitchen.jsonschema.metaschema.KeywordRegistry;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.annotations.Sets;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.*;

public abstract class AbstractFormatAttributeTest
{
    private final JsonNode testData;
    private final String fmt;
    private final Map<String, FormatAttribute> formatAttributes;

    private FormatAttribute formatAttribute;

    protected AbstractFormatAttributeTest(final KeywordRegistry registry,
        final String prefix, final String fmt)
        throws IOException
    {
        final String resource = "/format/" + prefix + '/' + fmt + ".json";
        testData = JsonLoader.fromResource(resource);
        formatAttributes = registry.getFormatAttributes();
        this.fmt = fmt;
    }

    @Test
    public final void formatAttributeExists()
    {
        formatAttribute = formatAttributes.get(fmt);
        assertNotNull(formatAttribute, "no such format attribute " + fmt);
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

    @Test(
        dataProvider = "getData",
        invocationCount = 10,
        threadPoolSize = 4,
        dependsOnMethods = "formatAttributeExists"
    )
    public final void testFormatAttribute(final JsonNode data,
        final boolean valid)
    {
        final ValidationReport report = new ValidationReport();

        formatAttribute.checkValue(fmt, report, data);

        assertEquals(report.isSuccess(), valid);
    }
}
