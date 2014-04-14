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

package com.github.fge.jsonschema.examples;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

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
