package com.github.fge.jsonschema.exceptions;

import com.github.fge.jsonschema.report.LogLevel;
import com.github.fge.jsonschema.report.ProcessingMessage;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.testng.Assert.*;

public final class ProcessingExceptionTest
{
    private static final String FOO = "foo";

    @Test
    public void thrownProcessingMessagesHaveLevelFatal()
    {
        final ProcessingMessage message = new ProcessingMessage();
        new ProcessingException(message);
        assertMessage(message).hasLevel(LogLevel.FATAL);
    }

    @Test
    public void processingExceptionMessageIsSameAsProcessingMessage()
    {
        final ProcessingMessage message = new ProcessingMessage()
            .message(FOO);
        final ProcessingException exception = new ProcessingException(message);
        assertEquals(exception.getMessage(), FOO);
    }

    @Test
    public void innerExceptionClassAndMessageAreReported()
    {
        final Exception inner = new Foo(FOO);
        final ProcessingException exception
            = new ProcessingException("", inner);
        final ProcessingMessage message = exception.getProcessingMessage();
        assertMessage(message).hasField("exceptionClass", Foo.class.getName())
            .hasField("exceptionMessage", inner.getMessage());
    }

    private static class Foo
        extends Exception
    {
        private Foo(final String message)
        {
            super(message);
        }
    }
}
