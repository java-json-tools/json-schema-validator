package eel.kitchen.jsonschema.validators.errors;

import eel.kitchen.jsonschema.validators.AbstractValidator;

public final class IllegalSchemaValidator
    extends AbstractValidator
{
    public IllegalSchemaValidator(final Exception e)
    {
        validationErrors.add(String.format("BROKEN SCHEMA: %s: %s",
            e.getClass().getSimpleName(), e.getMessage()));
    }
}