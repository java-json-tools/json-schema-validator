package eel.kitchen.jsonschema.validators.providers;

import eel.kitchen.jsonschema.validators.type.ObjectValidator;

public final class ObjectValidatorProvider
    extends AbstractValidatorProvider
{
    public ObjectValidatorProvider()
    {
        super("object", ObjectValidator.class, false, false);
    }
}
