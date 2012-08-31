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
import com.google.common.collect.Lists;
import org.eel.kitchen.jsonschema.bundle.KeywordBundle;
import org.eel.kitchen.jsonschema.bundle.KeywordBundles;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.ref.SchemaContainer;
import org.eel.kitchen.jsonschema.ref.SchemaRegistry;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.uri.URIManager;
import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.eel.kitchen.jsonschema.validator.JsonValidatorCache;
import org.eel.kitchen.jsonschema.validator.ValidationContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.*;

public abstract class AbstractKeywordValidatorTest
{
    private final JsonValidatorCache cache;
    private final JsonNode testData;
    private final Constructor<? extends KeywordValidator> constructor;

    AbstractKeywordValidatorTest(final Class<? extends KeywordValidator> c,
        final String resourceName)
        throws IOException, NoSuchMethodException
    {
        final String input = "/keyword/" + resourceName + ".json";
        testData = JsonLoader.fromResource(input);

        constructor = c.getConstructor(JsonNode.class);

        final KeywordBundle bundle = KeywordBundles.defaultBundle();
        final URIManager manager = new URIManager();
        final SchemaRegistry registry = new SchemaRegistry(manager,
            URI.create(""));

        cache = new JsonValidatorCache(bundle, registry);
    }

    @DataProvider
    protected Iterator<Object[]> getData()
    {
        final Set<Object[]> set = new HashSet<Object[]>(testData.size());

        for (final JsonNode node: testData)
            set.add(mungeArguments(node));

        return set.iterator();
    }

    private static Object[] mungeArguments(final JsonNode node)
    {
        return new Object[] {
            node.get("schema"),
            node.get("data"),
            node.get("valid").booleanValue(),
            node.get("messages")
        };
    }

    @Test(dataProvider = "getData", invocationCount = 10, threadPoolSize = 4)
    public final void testKeyword(final JsonNode schema, final JsonNode data,
        final boolean valid, final JsonNode messages)
        throws InvocationTargetException, IllegalAccessException,
        InstantiationException, JsonSchemaException
    {
        final KeywordValidator validator = constructor.newInstance(schema);
        final ValidationReport report = new ValidationReport();

        final ValidationContext context
            = new ValidationContext(cache, new SchemaContainer(schema));
        validator.validate(context, report, data);

        assertEquals(report.isSuccess(), valid);

        if (valid)
            return;

        final List<JsonNode> actual
            = Lists.newArrayList(report.asJsonNode().get(""));
        final List<JsonNode> expected = Lists.newArrayList(messages);

        assertEqualsNoOrder(actual.toArray(), expected.toArray());
    }
}
