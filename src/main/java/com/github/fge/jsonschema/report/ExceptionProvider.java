package com.github.fge.jsonschema.report;

import com.github.fge.jsonschema.exceptions.ProcessingException;

public interface ExceptionProvider
{
    ProcessingException doException(final ProcessingMessage message);
}
