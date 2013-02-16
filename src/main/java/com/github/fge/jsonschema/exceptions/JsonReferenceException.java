package com.github.fge.jsonschema.exceptions;

import com.github.fge.jsonschema.report.ProcessingMessage;

public final class JsonReferenceException
    extends ProcessingException
{
    public JsonReferenceException(final ProcessingMessage message,
        final Throwable e)
    {
        super(message, e);
    }
}
