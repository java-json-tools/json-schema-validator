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

import static com.github.fge.jsonschema.main.JsonSchemaFactory.*;

/**
 * Fifth example: setting a URI namespace; relative URI resolution
 *
 * <p><a href="doc-files/Example5.java">link to source code</a></p>
 *
 * <p>This example demonstrates another capability of {@link JsonSchemaFactory}:
 * the ability to set a URI namespace. This requires to customize the factory,
 * and therefore go through {@link Builder}, and more precisely {@link
 * Builder#setNamespace(String)}. After this, {@link JsonSchemaFactory#fromURI(
 * String)} and assimilates will resolve all URIs against this namespace (which
 * is empty by default).</p>
 *
 * <p>The schemas are split in two:</p>
 *
 * <ul>
 *     <li>one describing fstab: <a href="doc-files/split/fstab.json">here</a>;
 *     </li>
 *     <li>another describing an entry: <a href="doc-files/split/mntent.json">
 *     here</a>.</li>
 * </ul>
 *
 * <p>The first refers to the second one via the relative URI {@code
 * mntent.json}. This works precisely because a URI namespace has been set: all
 * URIs are resolved against this namespace.</p>
 *
 * <p>Files validated, and the validation outputs, are the same as for {@link
 * Example2}.</p>
 */
public final class Example5
    extends ExampleBase
{
    private static final String NAMESPACE
        = "resource:/com/github/fge/jsonschema/examples/split/";

    public static void main(final String... args)
        throws IOException, JsonSchemaException
    {
        final JsonNode good = loadResource("/fstab-good.json");
        final JsonNode bad = loadResource("/fstab-bad.json");
        final JsonNode bad2 = loadResource("/fstab-bad2.json");

        final JsonSchemaFactory factory = JsonSchemaFactory.builder()
            .setNamespace(NAMESPACE).build();

        final JsonSchema schema = factory.fromURI("fstab.json");

        ValidationReport report;

        report = schema.validate(good);
        printReport(report);

        report = schema.validate(bad);
        printReport(report);

        report = schema.validate(bad2);
        printReport(report);
    }
}
