/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
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
import com.github.fge.jsonschema.crude.CrudeValidator;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.library.SchemaVersion;
import com.github.fge.jsonschema.load.Dereferencing;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.github.fge.jsonschema.util.JsonLoader;
import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.annotations.Sets;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.*;

public final class SelfValidationTest
{
    private static final CrudeValidator VALIDATOR;

    static {
        try {
            VALIDATOR = new CrudeValidator(SchemaVersion.DRAFTV3,
                Dereferencing.INLINE);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final JsonNode draftv3;
    private final JsonNode draftv4;

    public SelfValidationTest()
        throws IOException
    {
        draftv3 = JsonLoader.fromResource("/draftv3/schema");
        draftv4 = JsonLoader.fromResource("/draftv4/schema");

    }

    @DataProvider
    private Iterator<Object[]> getBaseSchemas()
        throws IOException
    {
        final List<Object[]> list = Lists.newArrayList();

        list.add(new Object[] { draftv3 } );
        list.add(new Object[] { draftv4 } );

        return list.iterator();
    }

    @Test(
        dataProvider = "getBaseSchemas",
        invocationCount = 5,
        threadPoolSize = 3
    )
    public void schemaValidatesItself(final JsonNode schema)
        throws ProcessingException
    {
        final ProcessingReport report = VALIDATOR.validate(schema, schema);
        assertTrue(report.isSuccess());
    }

    @DataProvider
    public Iterator<Object[]> getGoogleSchemas()
        throws IOException
    {
        final JsonNode googleAPI
            = JsonLoader.fromResource("/other/google-json-api.json");
        final Map<String, JsonNode> schemas
            = JacksonUtils.asMap(googleAPI.get("schemas"));

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
        throws ProcessingException
    {
        final ProcessingReport report = VALIDATOR.validate(draftv3, node);

        assertTrue(report.isSuccess(), "Google schema " + name + " failed to "
            + "validate");
    }
}
