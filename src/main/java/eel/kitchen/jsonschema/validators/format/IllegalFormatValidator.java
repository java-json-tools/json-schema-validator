package eel.kitchen.jsonschema.validators.format;

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

    @Override
    public boolean validate(final JsonNode node)
    {
        return false;
    }
}
