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
import org.eel.kitchen.jsonschema.metaschema.BuiltinSchemas;
import org.eel.kitchen.jsonschema.metaschema.MetaSchema;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.schema.AddressingMode;
import org.eel.kitchen.jsonschema.schema.SchemaContainer;
import org.eel.kitchen.jsonschema.schema.SchemaNode;
import org.eel.kitchen.jsonschema.schema.SchemaRegistry;
import org.eel.kitchen.jsonschema.uri.URIManager;
import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.eel.kitchen.jsonschema.validator.JsonValidator;
import org.eel.kitchen.jsonschema.validator.JsonValidatorCache;
import org.eel.kitchen.jsonschema.validator.ValidationContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.annotations.Sets;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.*;

public abstract class AbstractKeywordValidatorTest
{
    private static final URI BASE_URI = URI.create("");

    private final JsonValidatorCache validatorCache;
    private final SchemaRegistry schemaRegistry;
    private final JsonNode testData;

    protected AbstractKeywordValidatorTest(final BuiltinSchemas builtin,
        final String resourceName)
        throws IOException
    {
        final MetaSchema metaSchema = MetaSchema.copyOf(builtin);
        schemaRegistry = new SchemaRegistry(new URIManager(), BASE_URI,
            AddressingMode.CANONICAL);
        validatorCache = new JsonValidatorCache(metaSchema, schemaRegistry);
        testData = JsonLoader.fromResource("/keyword/" + resourceName
            + ".json");
    }

    @DataProvider
    protected final Iterator<Object[]> getData()
    {
        final Set<Object[]> set = Sets.newHashSet();

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

    @Test(dataProvider = "getData", invocationCount = 5, threadPoolSize = 3)
    public final void testKeyword(final JsonNode schema, final JsonNode data,
        final boolean valid, final JsonNode messages)
    {
        final SchemaContainer container = schemaRegistry.register(schema);
        final SchemaNode schemaNode = new SchemaNode(container, schema);
        final ValidationReport report = new ValidationReport();
        final JsonValidator validator = validatorCache.getValidator(schemaNode);
        final ValidationContext ctx = new ValidationContext(validatorCache);

        validator.validate(ctx, report, data);

        assertEquals(report.isSuccess(), valid);

        if (valid)
            return;

        final List<JsonNode> actual
            = Lists.newArrayList(report.asJsonObject().get(""));
        final List<JsonNode> expected = Lists.newArrayList(messages);

        assertEqualsNoOrder(actual.toArray(), expected.toArray());
    }
}
