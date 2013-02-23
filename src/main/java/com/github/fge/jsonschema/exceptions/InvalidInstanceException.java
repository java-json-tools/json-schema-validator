package com.github.fge.jsonschema.exceptions;

import com.github.fge.jsonschema.report.ProcessingMessage;

/**
 * Exception thrown by the validation process when an instance is invalid
 */
public final class InvalidInstanceException
    extends ProcessingException
{
    public InvalidInstanceException(final ProcessingMessage message)
    {
        super(message);
    }
}
