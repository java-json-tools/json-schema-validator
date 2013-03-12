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
import com.github.fge.jsonschema.load.URIDownloader;
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.load.configuration.LoadingConfigurationBuilder;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.report.ProcessingReport;

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
