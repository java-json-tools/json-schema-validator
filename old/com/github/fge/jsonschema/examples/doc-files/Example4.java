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
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaException;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.report.ValidationReport;

import java.io.IOException;

/**
 * Fourth example: schema loading via URIs, and subschema addressing
 *
 * <p><a href="doc-files/Example4.java">link to source code</a></p>
 *
 * <p><a href="doc-files/fstab-sub.json">link to schema</a></p>
 *
 * <p>This demonstrates three capabilities of {@link JsonSchemaFactory}:</p>
 *
 * <ul>
 *     <li>the ability to load schemas via URIs;</li>
 *     <li>the ability to address subschemas in a schema;</li>
 *     <li>the ability to detect schema versions via {@code $schema} (as in
 *     {@link Example2}).</li>
 * </ul>
 *
 * <p>The implementation provides a {@code resource} scheme which allows to load
 * JSON from files in the classpath. It is strictly equivalent to calling {@link
 * Class#getResourceAsStream(String)}. The URI used is {@code
 * resource:/org/eel/kitchen/jsonschema/examples/fstab-sub.json}. Because we
 * want to validate against the {@code fstab} subschema, we use {@link
 * JsonSchemaFactory#fromURI(String, String)} to load the actual schema, with
 * the second argument being JSON Pointer {@code /fstab}. Note that unlike
 * methods which load schemas directly from JSON, this method and other similar
 * methods can throw {@link JsonSchemaException}.</p>
 *
 * <p>Since the root schema declares {@code $schema} to be draft v4, the set of
 * validators used will be validators defined for draft v4 (instead of the
 * default draft v3).</p>
 *
 * <p>Files validated, and the validation outputs, are the same as for {@link
 * Example2}.</p>
 */
public final class Example4
    extends ExampleBase
{
    private static final String SCHEMA_URI
        = "resource:/com/github/fge/jsonschema/examples/fstab-sub.json";

    public static void main(final String... args)
        throws IOException, JsonSchemaException
    {
        final JsonNode good = loadResource("/fstab-good.json");
        final JsonNode bad = loadResource("/fstab-bad.json");
        final JsonNode bad2 = loadResource("/fstab-bad2.json");

        final JsonSchemaFactory factory = JsonSchemaFactory.defaultFactory();

        final JsonSchema schema = factory.fromURI(SCHEMA_URI, "/fstab");

        ValidationReport report;

        report = schema.validate(good);
        printReport(report);

        report = schema.validate(bad);
        printReport(report);

        report = schema.validate(bad2);
        printReport(report);
    }
}
