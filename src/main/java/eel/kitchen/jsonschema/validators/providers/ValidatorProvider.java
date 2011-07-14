package eel.kitchen.jsonschema.validators.providers;

import eel.kitchen.jsonschema.validators.Validator;
import org.codehaus.jackson.JsonNode;

public interface ValidatorProvider
{
    void setSchema(final JsonNode schema);

    Validator getValidator();
}
