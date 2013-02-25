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
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.report.ProcessingReport;

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
    extends ExampleBase
{
    public static void main(final String... args)
        throws IOException, ProcessingException
    {
        final JsonNode fstabSchema = loadResource("/fstab.json");
        final JsonNode good = loadResource("/fstab-good.json");
        final JsonNode bad = loadResource("/fstab-bad.json");
        final JsonNode bad2 = loadResource("/fstab-bad2.json");

        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();

        final JsonSchema schema = factory.getJsonSchema(fstabSchema);

        ProcessingReport report;

        report = schema.validate(good);
        printReport(report);

        report = schema.validate(bad);
        printReport(report);

        report = schema.validate(bad2);
        printReport(report);
    }
}
