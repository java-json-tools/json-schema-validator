package eel.kitchen.jsonschema.validators.providers;

import eel.kitchen.jsonschema.validators.type.NumberValidator;

public final class NumberValidatorProvider
    extends AbstractValidatorProvider
{
    public NumberValidatorProvider()
    {
        super("number", NumberValidator.class, true, true);
    }
}
