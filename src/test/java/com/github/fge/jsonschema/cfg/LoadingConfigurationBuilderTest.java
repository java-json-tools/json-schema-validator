package com.github.fge.jsonschema.cfg;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.exceptions.JsonReferenceException;
import com.github.fge.jsonschema.exceptions.unchecked.LoadingConfigurationError;
import com.github.fge.jsonschema.load.URIDownloader;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.util.JacksonUtils;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Map;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static com.github.fge.jsonschema.messages.LoadingConfigurationMessages.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class LoadingConfigurationBuilderTest
{
    private static final String SAMPLE_ABSOLUTE_REF = "x://y";

    private final URIDownloader downloader = mock(URIDownloader.class);
    private final LoadingConfigurationBuilder cfg
        = LoadingConfiguration.newBuilder();

    @Test
    public void cannotRegisterNullScheme()
    {
        try {
            cfg.addScheme(null, downloader);
            fail("No exception thrown!!");
        } catch (LoadingConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_SCHEME);
        }
    }

    @Test
    public void cannotRegisterEmptyScheme()
    {
        try {
            cfg.addScheme("", downloader);
            fail("No exception thrown!!");
        } catch (LoadingConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(EMPTY_SCHEME);
        }
    }

    @Test
    public void cannotRegisterIllegalScheme()
    {
        final String scheme = "+24";
        try {
            cfg.addScheme(scheme, downloader);
            fail("No exception thrown!!");
        } catch (LoadingConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(ILLEGAL_SCHEME)
                .hasField("scheme", scheme);
        }
    }

    @Test
    public void registeringAndUnregisteringSchemeWorks()
    {
        final String scheme = "foo";

        cfg.addScheme(scheme, downloader);
        assertNotNull(cfg.freeze().getDownloaders().entries().get(scheme));

        cfg.removeScheme(scheme);
        assertNull(cfg.freeze().getDownloaders().entries().get(scheme));
    }

    @Test
    public void cannotSetNullDereferencingMode()
    {
        try {
            cfg.dereferencing(null);
            fail("No exception thrown!!");
        } catch (LoadingConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_DEREFERENCING_MODE);
        }
    }

    @Test
    public void redirectionsAreActuallyRegisteredAndConvertedToJsonRefs()
        throws JsonReferenceException
    {
        final String dest = "z://t#";
        final JsonRef sourceRef = JsonRef.fromString(SAMPLE_ABSOLUTE_REF);
        final JsonRef destinationRef = JsonRef.fromString(dest);
        cfg.addSchemaRedirect(SAMPLE_ABSOLUTE_REF, dest);

        final LoadingConfiguration frozen = cfg.freeze();
        assertEquals(frozen.getSchemaRedirects().get(sourceRef.getLocator()),
            destinationRef.getLocator());
    }

    @Test
    public void cannotRedirectToSelf()
        throws JsonReferenceException
    {
        try {
            cfg.addSchemaRedirect(SAMPLE_ABSOLUTE_REF, SAMPLE_ABSOLUTE_REF);
            fail("No exception thrown!!");
        } catch (LoadingConfigurationError e) {
            final URI uri = JsonRef.fromString(SAMPLE_ABSOLUTE_REF).toURI();
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(REDIRECT_TO_SELF)
                .hasField("uri", uri);
        }
    }

    @Test
    public void basicConfigurationContainsCoreSchemas()
    {
        final Map<URI, JsonNode> map = cfg.freeze().getPreloadedSchemas();

        URI uri;
        JsonNode node;

        for (final SchemaVersion version: SchemaVersion.values()) {
            uri = version.getLocation();
            node = version.getSchema();
            assertEquals(map.get(uri), node);
        }
    }

    @Test
    public void cannotOverwriteAnAlreadyPresentSchema()
    {
        final String input = "http://json-schema.org/draft-04/schema#";
        try {
            cfg.preloadSchema(input, JacksonUtils.nodeFactory().objectNode());
            fail("No exception thrown!!");
        } catch (LoadingConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(DUPLICATE_URI)
                .hasField("uri", input);
        }
    }

    @Test
    public void cannotPreloadSchemaWithoutTopLevelId()
    {
        try {
            cfg.preloadSchema(JacksonUtils.nodeFactory().objectNode());
            fail("No exception thrown!!");
        } catch (LoadingConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NO_ID_IN_SCHEMA);
        }
    }
}
