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

        BUNDLE = MessageBundle.newBuilder().appendSource(source).freeze();
    }

    private ValidationMessageBundle()
    {
    }

    public static MessageBundle get()
    {
        return BUNDLE;
    }
}
