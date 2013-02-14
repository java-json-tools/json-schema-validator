package com.github.fge.jsonschema.report;

import com.github.fge.jsonschema.exceptions.ExceptionProvider;
import com.github.fge.jsonschema.exceptions.ProcessingException;

public final class SimpleExceptionProvider
    implements ExceptionProvider
{
    private static final ExceptionProvider INSTANCE
        = new SimpleExceptionProvider();

    public static ExceptionProvider getInstance()
    {
        return INSTANCE;
    }

    private SimpleExceptionProvider()
    {
    }

    @Override
    public ProcessingException doException(final ProcessingMessage message)
    {
        return new ProcessingException(message);
    }
}
