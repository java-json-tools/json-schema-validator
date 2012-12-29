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
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eel.kitchen.jsonschema.main.JsonSchema;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.metaschema.BuiltinSchemas;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.annotations.Sets;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.*;

public final class SelfValidationTest
{
    private JsonSchemaFactory factory;

    @BeforeClass
    public void initFactory()
    {
        final JsonSchemaFactory.Builder builder = JsonSchemaFactory.builder();

        for (final BuiltinSchemas builtin: BuiltinSchemas.values())
            builder.addSchema(builtin.getURI(), builtin.getRawSchema());

        factory = builder.build();
    }

    @DataProvider
    private Iterator<Object[]> getBaseSchemas()
        throws IOException
    {
        final Set<Object[]> set = Sets.newHashSet();

        for (final BuiltinSchemas builtin: BuiltinSchemas.values())
            set.add(new Object[] { builtin });

        return set.iterator();
    }

    @Test(
        dataProvider = "getBaseSchemas",
        invocationCount = 5,
        threadPoolSize = 3
    )
    public void schemaValidatesItself(final BuiltinSchemas builtin)
        throws JsonSchemaException
    {
        final JsonNode rawSchema = builtin.getRawSchema();
        // It is assumed that all builtin schemas have a $schema
        final String dollarSchema = rawSchema.get("$schema").textValue();
        final JsonSchema schema = factory.fromURI(dollarSchema);
        final ValidationReport report = schema.validate(rawSchema);
        assertTrue(report.isSuccess(), builtin + " failed to validate "
            + "itself: " + report.getMessages());
    }

    @DataProvider
    public Iterator<Object[]> getGoogleSchemas()
        throws IOException
    {
        final JsonNode googleAPI
            = JsonLoader.fromResource("/other/google-json-api.json");
        final Map<String, JsonNode> schemas
            = ((ObjectNode) googleAPI.get("schemas")).asMap();

        final Set<Object[]> set = Sets.newHashSet();

        for (final Map.Entry<String, JsonNode> entry: schemas.entrySet())
            set.add(new Object[] { entry.getKey(), entry.getValue() });

        return set.iterator();
    }

    @Test(
        dataProvider = "getGoogleSchemas",
        invocationCount = 5,
        threadPoolSize = 3
    )
    public void testGoogleSchemas(final String name, final JsonNode node)
        throws JsonSchemaException
    {
        final JsonSchema schema
            = factory.fromURI(BuiltinSchemas.byDefault().getURI());
        final ValidationReport report = schema.validate(node);

        assertTrue(report.isSuccess(), "Google schema " + name + " failed to "
            + "validate");
    }
}
