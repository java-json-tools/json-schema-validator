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

package org.eel.kitchen.jsonschema.examples;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.JsonSchema;
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.report.ValidationReport;

import java.io.IOException;

/**
 * First example: basic usage
 *
 * <p><a href="doc-files/Example1.java">link to source code</a></p>
 *
 * <p>This shows a basic usage example. The schema used for validation is
 * <a href="doc-files/fstab.json">here</a>, which conforms to draft v3. You will
 * notice that a JSON Pointer ({@code #/mntent}) is used to address the mntent
 * subschema.</p>
 *
 * <p>This example uses {@link JsonSchemaFactory#defaultFactory()}, and uses
 * {@link JsonSchemaFactory#fromSchema(JsonNode)} to create the {@link
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
        throws IOException
    {
        final JsonNode fstabSchema = loadResource("/fstab.json");
        final JsonNode good = loadResource("/fstab-good.json");
        final JsonNode bad = loadResource("/fstab-bad.json");
        final JsonNode bad2 = loadResource("/fstab-bad2.json");

        final JsonSchemaFactory factory = JsonSchemaFactory.defaultFactory();

        final JsonSchema schema = factory.fromSchema(fstabSchema);

        ValidationReport report;

        report = schema.validate(good);
        printReport(report);

        report = schema.validate(bad);
        printReport(report);

        report = schema.validate(bad2);
        printReport(report);
    }
}
