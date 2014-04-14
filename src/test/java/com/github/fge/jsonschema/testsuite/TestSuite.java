/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.testsuite;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;
import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

@Test
public abstract class TestSuite
{
    private final JsonValidator validator;
    private final JsonNode testSuite;

    protected TestSuite(final SchemaVersion version, final String fileName)
        throws IOException
    {
        final ValidationConfiguration cfg = ValidationConfiguration.newBuilder()
            .setDefaultVersion(version).freeze();
        validator = JsonSchemaFactory.newBuilder()
            .setValidationConfiguration(cfg).freeze().getValidator();
        testSuite = JsonLoader.fromResource("/testsuite/" + fileName + ".json");
    }

    @DataProvider
    public final Iterator<Object[]> getAllTests()
    {
        final List<Object[]> list = Lists.newArrayList();

        for (final JsonNode test: testSuite)
            list.add(new Object[]{
                test.get("description").textValue(),
                test.get("schema"),
                test.get("data"),
                test.get("valid").booleanValue()
            });

        return list.iterator();
    }

    @Test(
        dataProvider = "getAllTests",
        invocationCount = 10,
        threadPoolSize = 4
    )
    public final void testsFromTestSuitePass(final String description,
        final JsonNode schema, final JsonNode data, final boolean valid)
        throws ProcessingException
    {
        final ProcessingReport report = validator.validate(schema, data);

        assertEquals(report.isSuccess(), valid,
            "test failed (description: " + description + ')');
    }
}
