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
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.ref.JsonRef;
import org.eel.kitchen.jsonschema.report.ValidationReport;

import java.io.IOException;
import java.net.URI;

import static org.eel.kitchen.jsonschema.main.JsonSchemaFactory.Builder;

/**
 * Tenth example: registering schemas
 *
 * <p><a href="doc-files/Example10.java">link to source code</a></p>
 *
 * <p>In this example, we register a custom schema into our factory with a
 * given URI, and initiate the schema instance using that URI.</p>
 *
 * <p>The only necessary condition for the URI is for it to be an absolute JSON
 * reference (see {@link JsonRef#isAbsolute()}), and you can register as many
 * schemas as you want. Here, we register both schemas from {@link Example5}.
 * You will notice that the scheme for these URIs is {@code xxx}: it does not
 * matter in the slightest that it is not a supported scheme by default, the
 * schema is registered all the same.</p>
 *
 * <p>This also shows that reference resolution still works in such a case,
 * since the {@code mntent} schema is referred to via a relative URI from the
 * {@code fstab} schema.</p>
 *
 * @see Builder#addSchema(String, JsonNode)
 * @see Builder#addSchema(URI, JsonNode)
 */
public final class Example10
    extends ExampleBase
{
    private static final String URI_BASE = "xxx://foo.bar/path/to/";

    public static void main(final String... args)
        throws IOException, JsonSchemaException
    {
        final JsonSchemaFactory.Builder builder
            = new JsonSchemaFactory.Builder();

        JsonNode node;
        String uri;

        node = loadResource("/split/fstab.json");
        uri = URI_BASE + "fstab.json";
        builder.addSchema(uri, node);

        node = loadResource("/split/mntent.json");
        uri = URI_BASE + "mntent.json";
        builder.addSchema(uri, node);

        final JsonSchemaFactory factory = builder.build();

        final JsonSchema schema = factory.fromURI(URI_BASE + "fstab.json");

        final JsonNode good = loadResource("/fstab-good.json");
        final JsonNode bad = loadResource("/fstab-bad.json");
        final JsonNode bad2 = loadResource("/fstab-bad2.json");

        ValidationReport report;

        report = schema.validate(good);
        printReport(report);

        report = schema.validate(bad);
        printReport(report);

        report = schema.validate(bad2);
        printReport(report);
    }
}
