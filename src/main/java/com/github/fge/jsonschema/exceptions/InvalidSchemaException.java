package com.github.fge.jsonschema.exceptions;

import com.github.fge.jsonschema.report.ProcessingMessage;

/**
 * Exception thrown by the validation process when a JSON Schema is invalid
 */
public final class InvalidSchemaException
    extends ProcessingException
{
    public InvalidSchemaException(final ProcessingMessage message)
    {
        super(message);
    }
}
