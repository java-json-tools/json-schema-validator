package com.github.fge.jsonschema.library;

import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.source.MessageSource;
import com.github.fge.msgsimple.source.PropertiesMessageSource;

import java.io.IOException;

public final class ValidationMessageBundle
{
    private static final MessageBundle BUNDLE;

    static {
        final MessageSource source;
        try {
            source = PropertiesMessageSource
                .fromResource("/validation.properties");
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }

        BUNDLE = new MessageBundle.Builder().appendSource(source).build();
    }

    private ValidationMessageBundle()
    {
    }

    public static MessageBundle get()
    {
        return BUNDLE;
    }
}
