package com.github.fge.jsonschema.exceptions.unchecked;

import com.github.fge.jsonschema.report.ProcessingMessage;

public final class LoadingConfigurationError
    extends ProcessingConfigurationError
{
    public LoadingConfigurationError()
    {
    }

    public LoadingConfigurationError(final ProcessingMessage message)
    {
        super(message);
    }
}
