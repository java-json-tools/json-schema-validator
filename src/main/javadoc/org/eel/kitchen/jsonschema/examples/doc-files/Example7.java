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
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.uri.URIDownloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static org.eel.kitchen.jsonschema.main.JsonSchemaFactory.*;

/**
 * Seventh example: custom URI scheme
 *
 * <p><a href="doc-files/Example7.java">link to source code</a></p>
 *
 * <p>This demonstrates {@link JsonSchemaFactory}'s ability to register a
 * custom URI scheme. In this example, the scheme is {@code foobar}, and it is
 * simply an alias to fetch a resource from the current package. As with all
 * customizations, you must go through {@link Builder} to do this.</p>
 *
 * <p>You can add a custom URI scheme by providing an implementation of {@link
 * URIDownloader}, and register it using {@link Builder#registerScheme(String,
 * URIDownloader)}. You are then able to fetch schemas via URIs (using {@link
 * JsonSchemaFactory#fromURI(URI)} or similar) using your custom scheme.</p>
 *
 * <p>The schema and files used are the same as for {@link Example2}.</p>
 */
public final class Example7
    extends ExampleBase
{
    public static void main(final String... args)
        throws IOException, JsonSchemaException
    {
        final JsonNode good = loadResource("/fstab-good.json");
        final JsonNode bad = loadResource("/fstab-bad.json");
        final JsonNode bad2 = loadResource("/fstab-bad2.json");

        final JsonSchemaFactory factory = new Builder()
            .registerScheme("foobar", CustomDownloader.getInstance())
            .build();

        final JsonSchema schema
            = factory.fromURI("foobar:/fstab-draftv4.json#");

        ValidationReport report;

        report = schema.validate(good);
        printReport(report);

        report = schema.validate(bad);
        printReport(report);

        report = schema.validate(bad2);
        printReport(report);
    }

    private static final class CustomDownloader
        implements URIDownloader
    {
        private static final String PREFIX;
        private static final URIDownloader instance = new CustomDownloader();

        static {
            final String pkgname = CustomDownloader.class.getPackage()
                .getName();
            PREFIX = '/' + pkgname.replace(".", "/");
        }

        public static URIDownloader getInstance()
        {
            return instance;
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
