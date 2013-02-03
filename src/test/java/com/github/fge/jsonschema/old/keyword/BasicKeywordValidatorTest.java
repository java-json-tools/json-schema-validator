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

package com.github.fge.jsonschema.old.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.SampleNodeProvider;
import com.github.fge.jsonschema.metaschema.BuiltinSchemas;
import com.github.fge.jsonschema.metaschema.MetaSchema;
import com.github.fge.jsonschema.old.keyword.KeywordValidator;
import com.github.fge.jsonschema.report.ValidationReport;
import com.github.fge.jsonschema.schema.AddressingMode;
import com.github.fge.jsonschema.schema.SchemaRegistry;
import com.github.fge.jsonschema.uri.URIManager;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.validator.JsonValidatorCache;
import com.github.fge.jsonschema.validator.ValidationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Iterator;

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
    private ValidationContext context;
    private ValidationReport report;
    private KeywordValidator validator;

    @BeforeMethod
    public void initContext()
    {
        final MetaSchema metaSchema
            = MetaSchema.copyOf(BuiltinSchemas.byDefault());
        final URIManager manager = new URIManager();
        final SchemaRegistry registry = new SchemaRegistry(manager,
            URI.create(""), AddressingMode.CANONICAL);
        final JsonValidatorCache cache = new JsonValidatorCache(metaSchema,
            registry);

        context = new ValidationContext(cache);
        report = new ValidationReport();
        validator = spy(new BasicKeywordValidator());
    }

    private static class BasicKeywordValidator
        extends KeywordValidator
    {
        private BasicKeywordValidator()
        {
            super("foo", NodeType.INTEGER, NodeType.NUMBER, NodeType.STRING);
        }

        @Override
        protected void validate(final ValidationContext context,
            final ValidationReport report, final JsonNode instance)
        {
        }

        @Override
        public final String toString()
        {
            return "KeywordValidator mock";
        }
    }

    @DataProvider
    public Iterator<Object[]> coveredInstances()
    {
        return SampleNodeProvider.getSamples(NodeType.INTEGER,
            NodeType.NUMBER, NodeType.STRING);
    }

    @Test(dataProvider = "coveredInstances")
    public void validateIsCalledOnCoveredInstances(final JsonNode instance)
    {
        validator.validateInstance(context, report, instance);
        verify(validator, times(1)).validate(context, report, instance);
    }

    @DataProvider
    public Iterator<Object[]> ignoredInstances()
    {
        return SampleNodeProvider.getSamplesExcept(NodeType.INTEGER,
            NodeType.NUMBER, NodeType.STRING);
    }

    @Test(dataProvider = "ignoredInstances")
    public void validateIsNotCalledOnIgnoredInstances(final JsonNode instance)
    {
        validator.validateInstance(context, report, instance);
        verify(validator, never()).validate(context, report, instance);
    }
}
