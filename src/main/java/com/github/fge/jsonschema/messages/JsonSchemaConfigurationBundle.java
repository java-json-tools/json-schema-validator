package com.github.fge.jsonschema.messages;

import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.bundle.PropertiesBundle;
import com.github.fge.msgsimple.serviceloader.MessageBundleProvider;

public final class JsonSchemaConfigurationBundle
    implements MessageBundleProvider
{
    private static final String PATH
        = "com/github/fge/jsonschema/validator/configuration.properties";

    @Override
    public MessageBundle getBundle()
    {
        return PropertiesBundle.forPath(PATH);
    }
}
