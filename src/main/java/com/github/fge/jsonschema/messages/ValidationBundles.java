package com.github.fge.jsonschema.messages;

import com.github.fge.jsonschema.exceptions.unchecked.FactoryConfigurationError;
import com.github.fge.jsonschema.exceptions.unchecked.ProcessingError;
import com.github.fge.jsonschema.exceptions.unchecked.ValidationConfigurationError;

public final class ValidationBundles
{
    public static final MessageBundle VALIDATION_CFG;
    public static final MessageBundle FACTORY_CFG;
    public static final MessageBundle FORMAT;

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

        name = "factoryCfg";
        provider = new MessageBundle.ErrorProvider()
        {
            @Override
            public ProcessingError doError(final String msg)
            {
                return new FactoryConfigurationError(msg);
            }
        };
        FACTORY_CFG = new MessageBundle(name, provider);

        provider = new MessageBundle.ErrorProvider()
        {
            @Override
            public ProcessingError doError(final String msg)
            {
                return new ProcessingError(msg);
            }
        };

        name = "format";
        FORMAT = new MessageBundle(name, provider);
    }

    private ValidationBundles()
    {
    }
}
