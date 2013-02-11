/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.jsonschema.other;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.library.DraftV3Library;
import com.github.fge.jsonschema.load.Dereferencing;
import com.github.fge.jsonschema.load.SchemaLoader;
import com.github.fge.jsonschema.load.URIManager;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ProcessorChain;
import com.github.fge.jsonschema.processors.data.FullValidationContext;
import com.github.fge.jsonschema.processors.data.ValidationData;
import com.github.fge.jsonschema.processors.ref.RefResolverProcessor;
import com.github.fge.jsonschema.processors.validation.ValidationChain;
import com.github.fge.jsonschema.processors.validation.ValidationProcessor;
import com.github.fge.jsonschema.report.ListProcessingReport;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.JsonTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.tree.SimpleJsonTree;
import com.github.fge.jsonschema.util.JsonLoader;
import com.google.common.io.Files;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
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

    private final ValidationChain chain
        = new ValidationChain(DraftV3Library.get());

    private URI rootURI;
    private File jarLocation;
    private String namespace;
    private JsonNode data;

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

    @Test
    public void callingSchemaViaAbsoluteJarURIWorks()
        throws ProcessingException
    {
        final SchemaLoader loader = buildLoader();
        final RefResolverProcessor refResolver
            = new RefResolverProcessor(loader);

        final Processor<ValidationData, FullValidationContext> processor
            = ProcessorChain.startWith(refResolver).chainWith(chain).end();

        final ValidationProcessor validator
            = new ValidationProcessor(processor);
        final String schemaPath = namespace + SCHEMA_SUBPATH;

        final SchemaTree tree = loader.get(URI.create(schemaPath));
        final JsonTree instance = new SimpleJsonTree(data);

        final ListProcessingReport listReport = new ListProcessingReport();
        final ProcessingReport out = validator.process(listReport,
            new ValidationData(tree, instance));

        assertTrue(out.isSuccess());
    }

    @Test
    public void callingSchemaViaJarURINamespaceWorks()
        throws ProcessingException
    {
        final SchemaLoader loader = buildLoader(URI.create(namespace));
        final RefResolverProcessor refResolver
            = new RefResolverProcessor(loader);

        final Processor<ValidationData, FullValidationContext> processor
            = ProcessorChain.startWith(refResolver).chainWith(chain).end();

        final ValidationProcessor validator
            = new ValidationProcessor(processor);
        final String schemaPath = namespace + SCHEMA_SUBPATH;

        final SchemaTree tree = loader.get(URI.create(schemaPath));
        final JsonTree instance = new SimpleJsonTree(data);

        final ListProcessingReport listReport = new ListProcessingReport();
        final ProcessingReport out = validator.process(listReport,
            new ValidationData(tree, instance));

        assertTrue(out.isSuccess());
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

    private static SchemaLoader buildLoader(final URI namespace)
    {
        return new SchemaLoader(new URIManager(), namespace,
            Dereferencing.CANONICAL);
    }

    private static SchemaLoader buildLoader()
    {
        return buildLoader(URI.create("#"));
    }
}
