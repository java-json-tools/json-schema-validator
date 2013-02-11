package com.github.fge.jsonschema.testsuite;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.crude.CrudeValidator;
import com.github.fge.jsonschema.library.SchemaVersion;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.processing.ref.Dereferencing;
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
    private static final JsonSchemaFactory FACTORY
        = JsonSchemaFactory.defaultFactory();

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
