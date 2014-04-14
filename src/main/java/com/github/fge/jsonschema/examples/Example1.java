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
 * First example: basic usage
 *
 * <p><a href="doc-files/Example1.java">link to source code</a></p>
 *
 * <p>This shows a basic usage example. The schema used for validation is
 * <a href="doc-files/fstab.json">here</a>, which conforms to draft v4, which is
 * the default version. You will notice that a JSON Pointer ({@code
 * #/definitions/mntent}) is used to address a subschema defining a mount entry.
 * </p>
 *
 * <p>This example uses {@link JsonSchemaFactory#byDefault()}, and uses
 * {@link JsonSchemaFactory#getJsonSchema(JsonNode)}  to create the {@link
 * JsonSchema} instance.</p>
 *
 * <p>The first sample (<a href="doc-files/fstab-good.json">here</a>) validates
 * successfully.</p>
 *
 * <p>The second sample (<a href="doc-files/fstab-bad.json">here</a>) fails to
 * validate. Please note that the failure occurs at the structural level
 * (required entry {@code swap} is missing). Validation therefore stops here,
 * and does not attempt to validate the {@code /} member of the instance, which
 * is itself invalid.</p>
 *
 * <p>The third sample (<a href="doc-files/fstab-bad2.json">here</a>) fails to
 * validate as well. This time, the problem is with the member values:</p>
 *
 * <ul>
 *     <li>the {@code options} member of {@code /tmp} is a string, but an array
 *     is expected;</li>
 *     <li>the {@code /} member is missing the required {@code fstype} member.
 *     </li>
 * </ul>
 */
public final class Example1
{
    public static void main(final String... args)
        throws IOException, ProcessingException
    {
        final JsonNode fstabSchema = Utils.loadResource("/fstab.json");
        final JsonNode good = Utils.loadResource("/fstab-good.json");
        final JsonNode bad = Utils.loadResource("/fstab-bad.json");
        final JsonNode bad2 = Utils.loadResource("/fstab-bad2.json");

        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();

        final JsonSchema schema = factory.getJsonSchema(fstabSchema);

        ProcessingReport report;

        report = schema.validate(good);
        System.out.println(report);

        report = schema.validate(bad);
        System.out.println(report);

        report = schema.validate(bad2);
        System.out.println(report);
    }
}
