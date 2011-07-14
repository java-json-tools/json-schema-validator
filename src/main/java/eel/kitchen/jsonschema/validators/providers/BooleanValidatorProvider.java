package eel.kitchen.jsonschema.validators.providers;

import eel.kitchen.jsonschema.validators.type.BooleanValidator;

public final class BooleanValidatorProvider
    extends AbstractValidatorProvider
{
    public BooleanValidatorProvider()
    {
        super("boolean", BooleanValidator.class, true, false);
    }
}
