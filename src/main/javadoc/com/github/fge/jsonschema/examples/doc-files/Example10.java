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
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfigurationBuilder;
import com.github.fge.jsonschema.core.ref.JsonRef;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import java.io.IOException;

/**
 * Tenth example: registering schemas
 *
 * <p><a href="doc-files/Example10.java">link to source code</a></p>
 *
 * <p>In this example, we register a custom schema with a given URI, and
 * initiate the {@link JsonSchema} instance using that URI. This is done by
 * customizing a {@link LoadingConfiguration} and registering schemas using
 * {@link LoadingConfigurationBuilder#preloadSchema(String, JsonNode)}.</p>
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
 */
public final class Example10
{
    private static final String URI_BASE = "xxx://foo.bar/path/to/";

    public static void main(final String... args)
        throws IOException, ProcessingException
    {
        final LoadingConfigurationBuilder builder
            = LoadingConfiguration.newBuilder();

        JsonNode node;
        String uri;

        node = Utils.loadResource("/split/fstab.json");
        uri = URI_BASE + "fstab.json";
        builder.preloadSchema(uri, node);

        node = Utils.loadResource("/split/mntent.json");
        uri = URI_BASE + "mntent.json";
        builder.preloadSchema(uri, node);

        final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
            .setLoadingConfiguration(builder.freeze()).freeze();

        final JsonSchema schema
            = factory.getJsonSchema(URI_BASE + "fstab.json");

        final JsonNode good = Utils.loadResource("/fstab-good.json");
        final JsonNode bad = Utils.loadResource("/fstab-bad.json");
        final JsonNode bad2 = Utils.loadResource("/fstab-bad2.json");

        ProcessingReport report;

        report = schema.validate(good);
        System.out.println(report);

        report = schema.validate(bad);
        System.out.println(report);

        report = schema.validate(bad2);
        System.out.println(report);
    }
}
