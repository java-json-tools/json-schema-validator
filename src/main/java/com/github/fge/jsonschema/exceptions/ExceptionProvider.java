package com.github.fge.jsonschema.exceptions;

import com.github.fge.jsonschema.report.ProcessingMessage;

public interface ExceptionProvider
{
    ProcessingException doException(final ProcessingMessage message);
}
