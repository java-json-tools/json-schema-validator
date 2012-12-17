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
import org.eel.kitchen.jsonschema.main.JsonSchema;
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.JacksonUtils;
import org.eel.kitchen.jsonschema.util.JsonLoader;
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
    private static final JsonSchemaFactory FACTORY;
    private static final JsonSchema DEFAULT_SCHEMA;

    static {
        final JsonNode node;

        try {
            node = JsonLoader.fromResource("/draftv3/schema");
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }

        FACTORY = JsonSchemaFactory.defaultFactory();
        DEFAULT_SCHEMA = FACTORY.fromSchema(node);
    }

    @DataProvider
    private Iterator<Object[]> getBaseSchemas()
        throws IOException
    {
        final Set<Object[]> set = Sets.newHashSet();

        String title;
        JsonNode schemaNode;

        title = "draft v3 core schema";
        schemaNode = JsonLoader.fromResource("/draftv3/schema");
        set.add(new Object[] { title, schemaNode });

        title = "draft v4 core schema";
        schemaNode = JsonLoader.fromResource("/draftv4/schema");
        set.add(new Object[] { title, schemaNode });

        return set.iterator();
    }

    @Test(
        dataProvider = "getBaseSchemas",
        invocationCount = 5,
        threadPoolSize = 3
    )
    public void schemaValidatesItself(final String title,
        final JsonNode schemaNode)
    {
        final JsonSchema schema = FACTORY.fromSchema(schemaNode);
        final ValidationReport report = schema.validate(schemaNode);
        assertTrue(report.isSuccess(), title + " failed to validate itself");
    }

    @DataProvider
    public Iterator<Object[]> getGoogleSchemas()
        throws IOException
    {
        final JsonNode googleAPI
            = JsonLoader.fromResource("/other/google-json-api.json");
        final Map<String, JsonNode> schemas
            = JacksonUtils.nodeToMap(googleAPI.get("schemas"));

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
    {
        final ValidationReport report = DEFAULT_SCHEMA.validate(node);

        assertTrue(report.isSuccess(), "Google schema " + name + " failed to "
            + "validate");
    }
}
