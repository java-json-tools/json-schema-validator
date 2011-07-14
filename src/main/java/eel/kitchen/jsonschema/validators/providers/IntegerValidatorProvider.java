package eel.kitchen.jsonschema.validators.providers;

import eel.kitchen.jsonschema.validators.type.IntegerValidator;

public final class IntegerValidatorProvider
    extends AbstractValidatorProvider
{
    public IntegerValidatorProvider()
    {
        super("integer", IntegerValidator.class, true, true);
    }
}
