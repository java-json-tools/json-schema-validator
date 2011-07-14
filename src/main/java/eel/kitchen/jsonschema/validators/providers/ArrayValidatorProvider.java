package eel.kitchen.jsonschema.validators.providers;

import eel.kitchen.jsonschema.validators.type.ArrayValidator;

public final class ArrayValidatorProvider
    extends AbstractValidatorProvider
{
    public ArrayValidatorProvider()
    {
        super("array", ArrayValidator.class, false, false);
    }
}
