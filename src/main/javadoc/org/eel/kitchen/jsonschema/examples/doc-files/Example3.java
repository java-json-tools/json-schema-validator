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
 * Third example: draft v4 detection via $schema
 *
 * <p><a href="doc-files/Example3.java">link to source code</a></p>
 *
 * <p>This shows a basic usage example. This is the same source code as for
 * {@link Example1}, except this time the schema (<a
 * href="doc-files/fstab-draftv4.json">here</a>) conforms to draft v4 instead of
 * draft v3 (the {@code $schema} value differs).</p>
 *
 * <p>One thing to note is a difference in the validation messages: while
 * required properties were in charge of the {@code properties} keyword for
 * draft v3, it is now in charge of the {@code required} keyword. The schema
 * could also be simplified (the regex in {@code patternProperties} now
 * recognizes {@code /}).</p>
 */
public final class Example3
    extends ExampleBase
{
    public static void main(final String... args)
        throws IOException
    {
        final JsonNode fstabSchema = loadResource("/fstab-draftv4.json");
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
