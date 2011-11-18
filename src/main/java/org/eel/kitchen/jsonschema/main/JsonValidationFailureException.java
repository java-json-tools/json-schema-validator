package org.eel.kitchen.jsonschema.main;

public final class JsonValidationFailureException
    extends Exception
{
    public JsonValidationFailureException()
    {
    }

    public JsonValidationFailureException(final String message)
    {
        super(message);
    }
}
