package com.github.fge.jsonschema.exceptions;

import com.github.fge.jsonschema.report.ProcessingMessage;

public final class InvalidInstanceException
    extends ProcessingException
{
    public InvalidInstanceException(final ProcessingMessage message)
    {
        super(message);
    }
}
