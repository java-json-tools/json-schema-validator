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

package org.eel.kitchen.jsonschema.other;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.io.Files;
import org.eel.kitchen.jsonschema.main.JsonSchema;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.testng.Assert.*;

public final class JarNamespaceValidationTest
{
    private static final String SCHEMA_SUBPATH = "child1/child.json";

    private URI rootURI;
    private File jarLocation;
    private String namespace;
    private JsonNode data;

    private JsonSchemaFactory.Builder builder;

    @BeforeClass
    public void buildJar()
        throws IOException, URISyntaxException
    {
        rootURI = getClass().getResource("/").toURI();
        jarLocation = doBuildJar();
        data = JsonLoader.fromResource("/grimbo/test-object.json");

        final String tmp = jarLocation.toURI().toString() + "!/grimbo/";
        namespace = new URI("jar", tmp, null).toString();
    }

    @BeforeTest
    public void setBuilder()
    {
        builder = new JsonSchemaFactory.Builder();
    }

    @Test
    public void callingSchemaViaAbsoluteJarURIWorks()
        throws URISyntaxException, JsonSchemaException
    {
        final JsonSchemaFactory factory = builder.build();
        final String schemaPath = namespace + SCHEMA_SUBPATH;

        final JsonSchema schema = factory.fromURI(schemaPath);

        final ValidationReport report = schema.validate(data);

        assertTrue(report.isSuccess());
    }

    @Test
    public void callingSchemaViaJarURINamespaceWorks()
        throws JsonSchemaException
    {
        final JsonSchemaFactory factory = builder.setNamespace(namespace)
            .build();

        final JsonSchema schema = factory.fromURI(SCHEMA_SUBPATH);

        final ValidationReport report = schema.validate(data);

        assertTrue(report.isSuccess());
    }

    @AfterClass
    public void deleteJar()
        throws IOException
    {
        if (!jarLocation.delete())
            throw new IOException("Could not delete " + jarLocation);
    }

    private File doBuildJar()
        throws IOException, URISyntaxException
    {
        final File tmpdir = new File(System.getProperty("java.io.tmpdir"));
        final File ret = new File(tmpdir, UUID.randomUUID().toString()
            + ".jar").getCanonicalFile();

        final URI baseURI = getClass().getResource("/grimbo").toURI();

        final FileOutputStream fh = new FileOutputStream(ret);
        final JarOutputStream jarfh = new JarOutputStream(fh);

        try {
            doWriteJar(jarfh, baseURI);
        } finally {
            fh.flush();
            jarfh.flush();

            jarfh.close();
            fh.close();
        }

        return ret;
    }

    private void doWriteJar(final JarOutputStream jarfh, final URI baseURI)
        throws IOException
    {
        final File src = new File(baseURI.getPath());
        String basePath = rootURI.relativize(baseURI).getPath();

        if (src.isDirectory() && !basePath.endsWith("/"))
            basePath += "/";

        final JarEntry entry = new JarEntry(basePath);
        jarfh.putNextEntry(entry);

        if (src.isDirectory()) {
            for (final File file: src.listFiles())
                doWriteJar(jarfh, file.toURI());
            return;
        }

        Files.copy(src, jarfh);
    }
}
