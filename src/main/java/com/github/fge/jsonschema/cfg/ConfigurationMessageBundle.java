package com.github.fge.jsonschema.cfg;

import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.source.MessageSource;
import com.github.fge.msgsimple.source.PropertiesMessageSource;

import java.io.IOException;

public final class ConfigurationMessageBundle
{
    private static final ConfigurationMessageBundle INSTANCE
        = new ConfigurationMessageBundle();

    private final MessageBundle bundle;

    public static ConfigurationMessageBundle getInstance()
    {
        return INSTANCE;
    }

    private ConfigurationMessageBundle()
    {
        final MessageSource source;

        try {
            source = PropertiesMessageSource
                .fromResource("/validationCfg.properties");
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }

        bundle = MessageBundle.newBuilder().appendSource(source).freeze();
    }

    public String getKey(final String key)
    {
        return bundle.getMessage(key);
    }

    public void checkNotNull(final Object obj, final String key)
    {
        if (obj == null)
            throw new NullPointerException(bundle.getMessage(key));
    }
}
