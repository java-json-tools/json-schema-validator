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

package com.github.fge.jsonschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.github.fge.jsonschema.util.JsonLoader;

import java.io.IOException;
import java.util.Map;

public final class NewAPIPerfTest
{
    private static final JsonValidator VALIDATOR;
    private static final JsonNode SCHEMA;

    static {
        VALIDATOR = JsonSchemaFactory.byDefault().getValidator();
        try {
            SCHEMA = JsonLoader.fromResource("/draftv4/schema");
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private NewAPIPerfTest()
    {
    }

    public static void main(final String... args)
        throws IOException, ProcessingException
    {
        final JsonNode googleAPI
            = JsonLoader.fromResource("/other/google-json-api.json");
        final Map<String, JsonNode> googleSchemas
            = JacksonUtils.asMap(googleAPI.get("schemas"));

        long begin, current;
        begin = System.currentTimeMillis();
        doValidate(googleSchemas, -1);
        current = System.currentTimeMillis();

        System.out.println("Initial validation :" + (current - begin) + " ms");

        begin = System.currentTimeMillis();
        for (int i = 0; i < 500; i++) {
            doValidate(googleSchemas, i);
            if (i % 20 == 0) {
                current = System.currentTimeMillis();
                System.out.println(String.format("Iteration %d (in %d ms)", i,
                    current - begin));
            }
        }

        final long end = System.currentTimeMillis();
        System.out.println("END -- time in ms: " + (end - begin));
        System.exit(0);
    }

    private static void doValidate(final Map<String, JsonNode> schemas,
        final int i)
        throws ProcessingException
    {
        String name;
        JsonNode value;
        ProcessingReport report;

        for (final Map.Entry<String, JsonNode> entry: schemas.entrySet()) {
            name = entry.getKey();
            value = entry.getValue();
            report = VALIDATOR.validate(SCHEMA, value);
            if (!report.isSuccess()) {
                System.err.println("ERROR: schema " + name + " did not "
                    + "validate (iteration " + i + ')');
                System.exit(1);
            }
        }
    }
}
