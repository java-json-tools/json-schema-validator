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
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

public final class BasicKeywordValidatorTest
{
    /*
     * This test is to check that validateValue() is only called on the
     * declared types of the keyword validator.
     *
     * Note that we don't bother calling the full mechanism (ie,
     * build via reflection).
     */
    private static final JsonNodeFactory factory = JsonNodeFactory.instance;

    private final JsonSchemaFactory schemaFactory
        = new JsonSchemaFactory.Builder().build();

    private ValidationContext context;
    private ValidationReport report;
    private KeywordValidator validator;

    @BeforeMethod
    public void initContext()
    {
        context = new ValidationContext(schemaFactory);
        report = new ValidationReport();
        validator = spy(new BasicKeywordValidator());
    }

    private static class BasicKeywordValidator
        extends KeywordValidator
    {
        BasicKeywordValidator()
        {
            super(NodeType.INTEGER, NodeType.NUMBER, NodeType.STRING);
        }

        @Override
        protected void validate(final ValidationContext context,
            final ValidationReport report, final JsonNode instance)
        {
        }
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
    public void validateIsCalledOnCoveredInstances(final JsonNode instance)
    {
        validator.validateInstance(context, report, instance);
        verify(validator, times(1)).validate(context, report, instance);
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
    public void validateIsNotCalledOnIgnoredInstances(final JsonNode instance)
    {
        validator.validateInstance(context, report, instance);
        verify(validator, never()).validate(context, report, instance);
    }
}
