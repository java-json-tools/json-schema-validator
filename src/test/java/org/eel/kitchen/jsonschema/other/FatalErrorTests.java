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

package org.eel.kitchen.jsonschema.other;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.eel.kitchen.jsonschema.bundle.Keyword;
import org.eel.kitchen.jsonschema.bundle.KeywordBundle;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.main.JsonSchema;
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.validator.ValidationContext;
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

    @Test(dataProvider = "getData", invocationCount = 10, threadPoolSize = 4)
    public void fataErrorsAreReportedAsSuch(final JsonNode node,
        final JsonNode data, final JsonNode message)
    {
        final JsonSchemaFactory factory = new JsonSchemaFactory.Builder()
            .build();
        final JsonSchema schema = factory.fromSchema(node);

        final ValidationReport report = schema.validate(data);

        assertTrue(report.hasFatalError());
        assertEquals(report.asJsonNode().iterator().next().get(0), message);
    }

    @Test(invocationCount = 10, threadPoolSize = 4)
    public void keywordBuildFailureRaisesFatalError()
    {
        // Build a bundle with only the failing validator
        final KeywordBundle bundle = new KeywordBundle();
        final Keyword foo = Keyword.Builder.forKeyword("foo")
            .withValidatorClass(Foo.class).build();

        bundle.registerKeyword(foo);

        // Build a new factory with that only keyword

        final JsonSchemaFactory factory = new JsonSchemaFactory.Builder()
            .withKeywordBundle(bundle).build();

        // Create our schema, which will also be our data, we don't care
        final JsonNode node = JsonNodeFactory.instance.objectNode()
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
