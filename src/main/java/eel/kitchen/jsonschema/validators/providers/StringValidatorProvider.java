package eel.kitchen.jsonschema.validators.providers;

import eel.kitchen.jsonschema.validators.type.StringValidator;

public final class StringValidatorProvider
    extends AbstractValidatorProvider
{
    public StringValidatorProvider()
    {
        super("string", StringValidator.class, true, true);
    }
}
