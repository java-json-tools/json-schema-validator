package eel.kitchen.jsonschema.validators.factories;

import eel.kitchen.jsonschema.validators.Validator;

public interface ValidatorFactory
{
    public Validator getValidator();
}
