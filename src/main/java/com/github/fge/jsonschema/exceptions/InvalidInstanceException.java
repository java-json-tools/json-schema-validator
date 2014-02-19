package com.github.fge.jsonschema.exceptions;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;

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
