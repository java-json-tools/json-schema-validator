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
import com.github.fge.jsonschema.cfg.LoadingConfiguration;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.load.Dereferencing;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonSchemaFactoryBuilder;
import com.github.fge.jsonschema.report.ProcessingReport;

import java.io.IOException;

/**
 * Second example: inline schema addressing
 *
 * <p><a href="doc-files/Example2.java">link to source code</a></p>
 *
 * <p>This example uses the same schema with one difference: the mntent
 * subschema is now referenced via inline addressing using an {@code id}.</p>
 *
 * <p>The schema used for validation is <a href="doc-files/fstab-inline.json">
 * here</a>.</p>
 *
 * <p>In order to use inline schema addressing, we cannot use the default
 * factory: we must go through a {@link JsonSchemaFactoryBuilder} and use a
 * modified {@link LoadingConfiguration} to tell that we want to use inline
 * dereferencing.</p>
 *
 * <p>Apart from these, the files used for validation and validation results
 * are the same as {@link Example1}.</p>
 *
 * @see Dereferencing
 */
public final class Example2
    extends ExampleBase
{
    public static void main(final String... args)
        throws IOException, ProcessingException
    {
        final JsonNode fstabSchema = loadResource("/fstab-inline.json");
        final JsonNode good = loadResource("/fstab-good.json");
        final JsonNode bad = loadResource("/fstab-bad.json");
        final JsonNode bad2 = loadResource("/fstab-bad2.json");

        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
            .dereferencing(Dereferencing.INLINE).freeze();
        final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
            .setLoadingConfiguration(cfg).freeze();

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
