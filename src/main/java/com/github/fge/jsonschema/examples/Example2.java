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
import com.github.fge.jsonschema.core.load.Dereferencing;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonSchemaFactoryBuilder;

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
{
    public static void main(final String... args)
        throws IOException, ProcessingException
    {
        final JsonNode fstabSchema = Utils.loadResource("/fstab-inline.json");
        final JsonNode good = Utils.loadResource("/fstab-good.json");
        final JsonNode bad = Utils.loadResource("/fstab-bad.json");
        final JsonNode bad2 = Utils.loadResource("/fstab-bad2.json");

        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
            .dereferencing(Dereferencing.INLINE).freeze();
        final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
            .setLoadingConfiguration(cfg).freeze();

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
