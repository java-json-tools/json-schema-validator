package com.github.fge.jsonschema.exceptions.unchecked;

import com.github.fge.jsonschema.report.ProcessingMessage;

/**
 * Exception thrown on loading configuration errors
 */
// TODO remove
public final class LoadingConfigurationError
    extends ProcessingConfigurationError
{
    public LoadingConfigurationError(final ProcessingMessage message)
    {
        super(message);
    }
}
