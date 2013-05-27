package com.github.fge.jsonschema.messages;

import com.github.fge.jsonschema.exceptions.unchecked.ProcessingError;
import com.github.fge.jsonschema.exceptions.unchecked.ValidationConfigurationError;

public final class ValidationBundles
{
    public final static MessageBundle VALIDATION_CFG;

    static {
        String name;
        MessageBundle.ErrorProvider provider;

        name = "validationCfg";
        provider = new MessageBundle.ErrorProvider()
        {
            @Override
            public ProcessingError doError(final String msg)
            {
                return new ValidationConfigurationError(msg);
            }
        };

        VALIDATION_CFG = new MessageBundle(name, provider);
    }

    private ValidationBundles()
    {
    }
}
