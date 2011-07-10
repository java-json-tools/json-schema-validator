package eel.kitchen.jsonschema.validators.errors;

import eel.kitchen.jsonschema.validators.AbstractValidator;
import org.codehaus.jackson.JsonNode;


public final class IllegalFormatValidator
    extends AbstractValidator
{
    public IllegalFormatValidator(final JsonNode ignored)
    {
        super(ignored);
        validationErrors.add("illegal format specification");
    }
}
