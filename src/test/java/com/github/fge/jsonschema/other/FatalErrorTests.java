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

package com.github.fge.jsonschema.other;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaException;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.Keyword;
import com.github.fge.jsonschema.metaschema.MetaSchema;
import com.github.fge.jsonschema.old.keyword.KeywordValidator;
import com.github.fge.jsonschema.report.ValidationReport;
import com.github.fge.jsonschema.util.JsonLoader;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import com.github.fge.jsonschema.validator.ValidationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.annotations.Sets;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.*;

public final class FatalErrorTests
{
    private JsonNode testData;

    @BeforeClass
    public void initData()
        throws IOException
    {
        testData = JsonLoader.fromResource("/other/fatal.json");
    }

    @DataProvider
    private Iterator<Object[]> getData()
    {
        final Set<Object[]> set = Sets.newHashSet();

        for (final JsonNode node: testData)
            set.add(new Object[] {
                node.get("schema"),
                node.get("data"),
                node.get("message")
            });

        return set.iterator();
    }

    @Test(dataProvider = "getData", invocationCount = 5, threadPoolSize = 3)
    public void fataErrorsAreReportedAsSuch(final JsonNode node,
        final JsonNode data, final JsonNode message)
    {
        final JsonSchemaFactory factory = JsonSchemaFactory.defaultFactory();
        final JsonSchema schema = factory.fromSchema(node);

        final ValidationReport report = schema.validate(data);

        assertTrue(report.hasFatalError());
        assertEquals(report.asJsonObject().iterator().next().get(0), message);
    }

    @Test(invocationCount = 5, threadPoolSize = 3)
    public void keywordBuildFailureRaisesFatalError()
        throws JsonSchemaException
    {
        final Keyword foo = Keyword.withName("foo")
            .withValidatorClass(Foo.class).build();

        final MetaSchema metaSchema = MetaSchema.builder()
            .withURI("foo://bar").addKeyword(foo).build();

        final JsonSchemaFactory factory = JsonSchemaFactory.builder()
            .addMetaSchema(metaSchema, true).build();

        // Create our schema, which will also be our data, we don't care
        final JsonNode node = JacksonUtils.nodeFactory().objectNode()
            .put("foo", "bar");

        final JsonSchema schema = factory.fromSchema(node);

        final ValidationReport report = schema.validate(node);

        assertTrue(report.hasFatalError());
    }

    private static final class Foo
        extends KeywordValidator
    {
        // Invalid constructor: no JsonNode argument
        private Foo()
        {
            super("foo", NodeType.values());
        }

        @Override
        protected void validate(final ValidationContext context,
            final ValidationReport report, final JsonNode instance)
        {
        }

        @Override
        public String toString()
        {
            return "foo";
        }
    }
}
