package com.github.fge.jsonschema.keyword.validator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.messages.JsonSchemaConfigurationBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;

/**
 * A validator factory that uses reflection to create an instance of the
 * specified KeywordValidator class
 */
public class ReflectionKeywordValidatorFactory
    implements KeywordValidatorFactory
{
    private static final String ERRMSG = "failed to build keyword validator";
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaConfigurationBundle.class);

    private final Constructor<? extends KeywordValidator> constructor;

    public ReflectionKeywordValidatorFactory(String name,
        Class<? extends KeywordValidator> clazz)
    {
        try {
            constructor = clazz.getConstructor(JsonNode.class);
        } catch (NoSuchMethodException ignored) {
            throw new IllegalArgumentException(BUNDLE.printf(
                "noAppropriateConstructor", name, clazz.getCanonicalName()
            ));
        }
    }

    @Override
    public KeywordValidator getKeywordValidator(JsonNode node)
        throws ProcessingException
    {
        try {
            return constructor.newInstance(node);
        } catch (InstantiationException e) {
            throw new ProcessingException(ERRMSG, e);
        } catch (IllegalAccessException e) {
            throw new ProcessingException(ERRMSG, e);
        } catch (InvocationTargetException e) {
            throw new ProcessingException(ERRMSG, e);
        }
    }

}
