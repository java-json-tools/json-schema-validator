package eel.kitchen.jsonschema.validators.format;

import org.codehaus.jackson.JsonNode;


public final class IllegalFormatValidator
    extends AbstractFormatValidator
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
