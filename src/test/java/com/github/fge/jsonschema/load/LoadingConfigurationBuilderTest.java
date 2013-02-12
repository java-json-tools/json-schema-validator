package com.github.fge.jsonschema.load;

import com.github.fge.jsonschema.exceptions.JsonReferenceException;
import com.github.fge.jsonschema.exceptions.unchecked.LoadingConfigurationError;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.report.ProcessingMessage;
import org.testng.annotations.Test;

import java.net.URI;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static com.github.fge.jsonschema.messages.LoadingMessages.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class LoadingConfigurationBuilderTest
{
    private static final String NOT_A_URI = "+24://x.y/z";
    private static final String RELATIVE_URI = "foo";
    private static final String SAMPLE_ABSOLUTE_REF = "x://y";

    private final URIDownloader downloader = mock(URIDownloader.class);
    private final LoadingConfigurationBuilder cfg
        = LoadingConfiguration.newConfiguration();

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
        assertNotNull(cfg.freeze().downloaders().get(scheme));

        cfg.removeScheme(scheme);
        assertNull(cfg.freeze().downloaders().get(scheme));
    }

    @Test
    public void cannotRegisterNullNamespace()
    {
        try {
            cfg.setNamespace(null);
            fail("No exception thrown!!");
        } catch (LoadingConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_URI);
        }
    }

    @Test
    public void namespaceCannotBeIllegalURI()
    {
        try {
            cfg.setNamespace(NOT_A_URI);
            fail("No exception thrown!:");
        } catch (LoadingConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(INVALID_URI)
                .hasField("input", NOT_A_URI);
        }
    }

    @Test
    public void cannotRegisterNonAbsoluteNamespace()
    {
        try {
            cfg.setNamespace(RELATIVE_URI);
            fail("No exception thrown!!");
        } catch (LoadingConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(REF_NOT_ABSOLUTE)
                .hasField("input", RELATIVE_URI);
        }
    }

    @Test
    public void redirectionSourceCannotBeNull()
    {
        try {
            cfg.addSchemaRedirect(null, null);
            fail("No exception thrown!!");
        } catch (LoadingConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_URI);
        }
    }

    @Test
    public void redirectionSourceCannotBeAnInvalidURI()
    {
        try {
            cfg.addSchemaRedirect(NOT_A_URI, null);
            fail("No exception thrown!!");
        } catch (LoadingConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(INVALID_URI)
                .hasField("input", NOT_A_URI);
        }
    }

    @Test
    public void redirectionSourceMustBeAbsolute()
    {
        final String input = "foo";
        try {
            cfg.addSchemaRedirect(input, null);
            fail("No exception thrown!!");
        } catch (LoadingConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(REF_NOT_ABSOLUTE)
                .hasField("input", input);
        }
    }

    @Test
    public void redirectionDestinationCannotBeNull()
    {
        try {
            cfg.addSchemaRedirect(SAMPLE_ABSOLUTE_REF, null);
            fail("No exception thrown!!");
        } catch (LoadingConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_URI);
        }
    }

    @Test
    public void redirectionDestinationCannotBeAnInvalidURI()
    {
        try {
            cfg.addSchemaRedirect(SAMPLE_ABSOLUTE_REF, NOT_A_URI);
            fail("No exception thrown!!");
        } catch (LoadingConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(INVALID_URI)
                .hasField("input", NOT_A_URI);
        }
    }

    @Test
    public void redirectionDestinationMustBeAbsolute()
    {
        final String input = "foo";
        try {
            cfg.addSchemaRedirect(SAMPLE_ABSOLUTE_REF, input);
            fail("No exception thrown!!");
        } catch (LoadingConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(REF_NOT_ABSOLUTE)
                .hasField("input", input);
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
        assertEquals(frozen.schemaRedirects().get(sourceRef.getLocator()),
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
}
