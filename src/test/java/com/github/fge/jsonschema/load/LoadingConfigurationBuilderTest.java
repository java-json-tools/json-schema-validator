package com.github.fge.jsonschema.load;

import com.github.fge.jsonschema.exceptions.unchecked.LoadingConfigurationError;
import com.github.fge.jsonschema.report.ProcessingMessage;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static com.github.fge.jsonschema.messages.LoadingMessages.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class LoadingConfigurationBuilderTest
{
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
}
