package com.github.fge.jsonschema.exceptions.unchecked;

import com.github.fge.jsonschema.report.LogLevel;
import com.github.fge.jsonschema.report.ProcessingMessage;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.testng.Assert.*;

public final class ProcessingConfigurationErrorTest
{
    private static final String FOO = "foo";

    @Test
    public void thrownProcessingMessagesHaveLevelFatal()
    {
        final ProcessingMessage message = new ProcessingMessage();
        new ProcessingConfigurationError(message);
        assertMessage(message).hasLevel(LogLevel.FATAL);
    }

    @Test
    public void processingExceptionMessageIsSameAsProcessingMessage()
    {
        final ProcessingMessage message = new ProcessingMessage()
            .message(FOO);
        final ProcessingConfigurationError exception
            = new ProcessingConfigurationError(message);
        assertEquals(exception.getMessage(), FOO);
    }
}
