package eel.kitchen.jsonschema.validators.providers;

import eel.kitchen.jsonschema.validators.Validator;

public interface ValidatorProvider
{
    Validator getValidator();
}
