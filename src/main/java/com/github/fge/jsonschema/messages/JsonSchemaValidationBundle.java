package com.github.fge.jsonschema.messages;

import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.bundle.PropertiesBundle;
import com.github.fge.msgsimple.load.MessageBundleLoader;

public final class JsonSchemaValidationBundle
    implements MessageBundleLoader
{
    private static final String PATH
        = "com/github/fge/jsonschema/validator/validation.properties";

    @Override
    public MessageBundle getBundle()
    {
        return PropertiesBundle.forPath(PATH);
    }
}
