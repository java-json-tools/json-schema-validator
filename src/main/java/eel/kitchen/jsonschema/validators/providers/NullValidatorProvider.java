package eel.kitchen.jsonschema.validators.providers;

import eel.kitchen.jsonschema.validators.type.NullValidator;

public final class NullValidatorProvider
    extends AbstractValidatorProvider
{
    public NullValidatorProvider()
    {
        super("null", NullValidator.class, false, false);
    }
}
