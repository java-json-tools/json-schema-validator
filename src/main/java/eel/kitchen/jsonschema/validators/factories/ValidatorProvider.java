package eel.kitchen.jsonschema.validators.factories;

import eel.kitchen.jsonschema.validators.Validator;

public interface ValidatorProvider
{
    Validator getValidator();
}
