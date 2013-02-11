package com.github.fge.jsonschema.exceptions;

import com.github.fge.jsonschema.report.ProcessingMessage;

public final class InvalidSchemaException
    extends ProcessingException
{
    public InvalidSchemaException(final ProcessingMessage message)
    {
        super(message);
    }
}
