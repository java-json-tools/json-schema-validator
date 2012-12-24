package org.eel.kitchen.jsonschema.testsuite;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import org.eel.kitchen.jsonschema.main.JsonSchema;
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.testng.Assert.assertEquals;

public final class OfficialTestSuite
{
    private static final String VERSION = "1.0.0";
    // The slash is necessary since we read it as a resource
    private static final String SUITE_NAME = "/%s.zip";
    private static final String TEST_BASE = "JSON-Schema-Test-Suite-%s/tests";

    private static final JsonSchemaFactory FACTORY
        = JsonSchemaFactory.defaultFactory();

    private File baseDir;
    private Set<File> allTests;

    @BeforeClass
    public void extractTestSuite()
        throws IOException
    {
        // Throws IllegalStateException if the directory cannot be created
        baseDir = Files.createTempDir().getCanonicalFile();

        final String suiteName = String.format(SUITE_NAME, VERSION);
        final File suite = new File(OfficialTestSuite.class
            .getResource(suiteName).getPath()).getCanonicalFile();

        try {
            doExtract(suite);
            final File testBase = new File(baseDir,
                String.format(TEST_BASE, VERSION));
            if (!testBase.isDirectory())
                throw new IOException("Expected test base to be a directory");
            allTests = lsDashLr(testBase);
        } catch (IOException e) {
            rmDashRf(baseDir);
            throw e;
        }
    }

    @DataProvider
    public Iterator<Object[]> getAllTests()
        throws IOException
    {
        String desc;
        JsonNode node, schema, data;
        String testName;
        boolean valid;

        final Set<Object[]> set = Sets.newHashSet();

        for (final File testFile: allTests) {
            node = JsonLoader.fromFile(testFile);
            for (final JsonNode test: node) {
                desc = test.get("description").textValue();
                schema = test.get("schema");
                for (final JsonNode element: test.get("tests")) {
                    testName = element.get("description").textValue();
                    data = element.get("data");
                    valid = element.get("valid").booleanValue();
                    set.add(new Object[] {
                        "description: " + desc + "; test: " + testName,
                        schema,
                        data,
                        valid
                    });
                }
            }
        }

        return set.iterator();
    }

    @Test(
        dataProvider = "getAllTests",
        invocationCount = 5,
        threadPoolSize = 2
    )
    public void testValidatesOK(final String desc, final JsonNode schema,
        final JsonNode data, final boolean valid)
    {
        final JsonSchema jsonSchema = FACTORY.fromSchema(schema);
        final ValidationReport report = jsonSchema.validate(data);

        assertEquals(report.isSuccess(), valid, "test failure: " + desc);
    }

    @AfterClass
    public void deleteSuite()
        throws IOException
    {
        rmDashRf(baseDir);
    }

    private void doExtract(final File suite)
        throws IOException
    {
        InputStream in;
        ZipEntry entry;
        File realEntry;

        final ZipFile zipFile;

        zipFile = new ZipFile(suite);
        try {
            final Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                entry = entries.nextElement();
                realEntry = new File(baseDir, entry.getName())
                    .getCanonicalFile();
                if (entry.isDirectory()) {
                    if (!realEntry.mkdir())
                        throw new IOException("Failed to create directory"
                            + realEntry);
                    continue;
                }
                in = zipFile.getInputStream(entry);
                try {
                    doCopy(in, realEntry);
                } finally {
                    in.close();
                }
            }
        } finally {
            zipFile.close();
        }
    }

    private static void rmDashRf(final File victim)
        throws IOException
    {
        if (victim.isDirectory())
            for (final File file: victim.listFiles())
                rmDashRf(file);
        if (!victim.delete())
            throw new IOException("cannot delete file " + victim);
    }

    private static void doCopy(final InputStream in, final File dst)
        throws IOException
    {
        final byte[] buf = new byte[1024];
        final FileOutputStream out;

        int size;

        out = new FileOutputStream(dst);
        try {
            while ((size = in.read(buf)) != -1)
                out.write(buf, 0, size);
            out.flush();
        } finally {
            out.close();
        }
    }

    private static Set<File> lsDashLr(final File base)
    {
        final Set<File> ret = Sets.newHashSet();
        doLsDashLr(base, ret);
        return ret;
    }

    private static void doLsDashLr(final File base, final Set<File> ret)
    {
        if (base.isFile()) {
            ret.add(base);
            return;
        }

        for (final File entry: base.listFiles())
            doLsDashLr(entry, ret);
    }
}
