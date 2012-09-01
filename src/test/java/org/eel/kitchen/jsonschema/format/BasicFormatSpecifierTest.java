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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.validator.ValidationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

public final class BasicFormatSpecifierTest
{
    /*
     * Here we create a basic FormatSpecifier covering a certain type set,
     * and check that the checkValue() method is only called for this type set.
     *
     * We pick a set of covered types in advance.
     */
    private static final JsonNodeFactory factory = JsonNodeFactory.instance;
    private static final String FMT = "fmt";

    private final ValidationContext ctx = new ValidationContext(null);

    private ValidationReport report;
    private FormatSpecifier specifier;

    private static class CustomFormatSpecifier
        extends FormatSpecifier
    {

        private CustomFormatSpecifier()
        {
            super(NodeType.INTEGER, NodeType.NUMBER, NodeType.STRING);
        }

        @Override
        public void checkValue(final String fmt, final ValidationContext ctx,
            final ValidationReport report, final JsonNode value)
        {
        }
    }

    @BeforeMethod
    public void ctxInit()
    {
        report = new ValidationReport();
        specifier = spy(new CustomFormatSpecifier());
    }

    @DataProvider
    public Object[][] coveredInstances()
    {
        return new Object[][] {
            { factory.numberNode(0) },
            { factory.numberNode(new BigDecimal("1.1"))},
            { factory.textNode("") }
        };
    }

    @Test(dataProvider = "coveredInstances")
    public void checkValueIsCalledOnCoveredInstances(final JsonNode instance)
    {
        specifier.validate(FMT, ctx, report, instance);
        verify(specifier, times(1)).checkValue(FMT, ctx, report, instance);
    }

    @DataProvider
    public Object[][] ignoredInstances()
    {
        return new Object[][] {
            { factory.nullNode() },
            { factory.arrayNode() },
            { factory.objectNode() },
            { factory.booleanNode(true) }
        };
    }

    @Test(dataProvider = "ignoredInstances")
    public void checkValueIsNotCalledOnIgnoredInstances(final JsonNode instance)
    {
        specifier.validate(FMT, ctx, report, instance);
        verify(specifier, never()).checkValue(FMT, ctx, report, instance);
    }
}
