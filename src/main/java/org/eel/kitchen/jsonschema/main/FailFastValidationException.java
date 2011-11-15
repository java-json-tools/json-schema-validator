package org.eel.kitchen.jsonschema.main;

public final class FailFastValidationException
    extends IllegalArgumentException
{
    public FailFastValidationException(final String s)
    {
        super(s);
    }
}
