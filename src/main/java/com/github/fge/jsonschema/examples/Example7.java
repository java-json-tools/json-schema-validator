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
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfigurationBuilder;
import com.github.fge.jsonschema.core.load.download.URIDownloader;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Seventh example: custom URI scheme
 *
 * <p><a href="doc-files/Example7.java">link to source code</a></p>
 *
 * <p>This demonstrates {@link JsonSchemaFactory}'s ability to register a
 * custom URI scheme. In this example, the scheme is {@code foobar}, and it is
 * simply an alias to fetch a resource from the current package.</p>
 *
 * <p>Two things are needed:</p>
 *
 * <ul>
 *     <li>an implementation of {@link URIDownloader} for this scheme,</li>
 *     <li>registering this scheme using {@link
 *     LoadingConfigurationBuilder#addScheme(String, URIDownloader)}.</li>
 * </ul>
 *
 * <p>Once this is done, this scheme, when encountered anywhere in JSON
 * References, will use this downloader, and you are also able to use it when
 * loading schemas using {@link JsonSchemaFactory#getJsonSchema(String)}, which
 * is what this example does.</p>
 *
 * <p>The schema and files used are the same as for {@link Example2}.</p>
 */
public final class Example7
{
    public static void main(final String... args)
        throws IOException, ProcessingException
    {
        final JsonNode good = Utils.loadResource("/fstab-good.json");
        final JsonNode bad = Utils.loadResource("/fstab-bad.json");
        final JsonNode bad2 = Utils.loadResource("/fstab-bad2.json");

        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
            .addScheme("foobar", CustomDownloader.getInstance()).freeze();

        final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
            .setLoadingConfiguration(cfg).freeze();

        final JsonSchema schema
            = factory.getJsonSchema("foobar:/fstab.json#");

        ProcessingReport report;

        report = schema.validate(good);
        System.out.println(report);

        report = schema.validate(bad);
        System.out.println(report);

        report = schema.validate(bad2);
        System.out.println(report);
    }

    private static final class CustomDownloader
        implements URIDownloader
    {
        private static final String PREFIX;
        private static final URIDownloader INSTANCE = new CustomDownloader();

        static {
            final String pkgname = CustomDownloader.class.getPackage()
                .getName();
            PREFIX = '/' + pkgname.replace(".", "/");
        }

        public static URIDownloader getInstance()
        {
            return INSTANCE;
        }

        @Override
        public InputStream fetch(final URI source)
            throws IOException
        {
            final String path = PREFIX + source.getPath();
            final InputStream ret = getClass().getResourceAsStream(path);

            if (ret == null)
                throw new IOException("resource " + path + " not found");
            return ret;
        }
    }
}
