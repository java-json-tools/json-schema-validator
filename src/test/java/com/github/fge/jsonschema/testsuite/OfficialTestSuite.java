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

package com.github.fge.jsonschema.testsuite;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.crude.CrudeValidator;
import com.github.fge.jsonschema.library.SchemaVersion;
import com.github.fge.jsonschema.load.Dereferencing;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.JsonLoader;
import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

public final class OfficialTestSuite
{
    private static final CrudeValidator VALIDATOR;

    static {
        try {
            VALIDATOR = new CrudeValidator(SchemaVersion.DRAFTV3,
                Dereferencing.CANONICAL);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final JsonNode testSuite;

    public OfficialTestSuite()
        throws IOException
    {
        testSuite = JsonLoader.fromResource("/testsuite.json");
    }

    @DataProvider
    public Iterator<Object[]> allTests()
    {
        final List<Object[]> list = Lists.newArrayList();

        for (final JsonNode element: testSuite)
            list.add(new Object[] {
                element.get("desc").textValue(),
                element.get("schema"),
                element.get("data"),
                element.get("valid").booleanValue()
            });

        Collections.shuffle(list);
        return list.iterator();
    }

    @Test(
        dataProvider = "allTests",
        threadPoolSize = 3,
        invocationCount = 10
    )
    public void testValidatesOK(final String desc, final JsonNode schema,
        final JsonNode data, final boolean valid)
    {
        final ProcessingReport report
            = VALIDATOR.validateUnchecked(schema, data);

        assertEquals(report.isSuccess(), valid, "test failure: " + desc);
    }
}
