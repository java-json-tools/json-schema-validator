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

package com.github.fge.jsonschema.examples;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.report.ProcessingReport;

import java.io.IOException;

/**
 * Fourth example: schema loading via URIs, and subschema addressing
 *
 * <p><a href="doc-files/Example4.java">link to source code</a></p>
 *
 * <p><a href="doc-files/fstab-sub.json">link to schema</a></p>
 *
 * <p>This demonstrates two capabilities of {@link JsonSchemaFactory}:</p>
 *
 * <ul>
 *     <li>the ability to Utils.load schemas via URIs;</li>
 *     <li>the ability to address subschemas in a schema.</li>
 * </ul>
 *
 * <p>The implementation provides a {@code resource} scheme which allows to Utils.load
 * JSON from files in the classpath. It is strictly equivalent to calling {@link
 * Class#getResourceAsStream(String)}.</p>
 *
 * <p>The URI used is {@code
 * resource:/org/eel/kitchen/jsonschema/examples/fstab-sub.json}. Because we
 * want to validate against the {@code fstab} subschema, we use {@link
 * JsonSchemaFactory#getJsonSchema(String)} to Utils.load the actual schema; the URI
 * used as an argument also has a JSON Pointer as a fragment.</p>
 *
 * <p>Files validated, and the validation outputs, are the same as for {@link
 * Example2}.</p>
 */
public final class Example4
{
    private static final String SCHEMA_URI
        = "resource:/com/github/fge/jsonschema/examples/fstab-sub.json#/fstab";

    public static void main(final String... args)
        throws IOException, ProcessingException
    {
        final JsonNode good = Utils.loadResource("/fstab-good.json");
        final JsonNode bad = Utils.loadResource("/fstab-bad.json");
        final JsonNode bad2 = Utils.loadResource("/fstab-bad2.json");

        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();

        final JsonSchema schema = factory.getJsonSchema(SCHEMA_URI);

        ProcessingReport report;

        report = schema.validate(good);
        System.out.println(report);

        report = schema.validate(bad);
        System.out.println(report);

        report = schema.validate(bad2);
        System.out.println(report);
    }
}
