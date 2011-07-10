package eel.kitchen.jsonschema.validators.format;

import eel.kitchen.jsonschema.validators.AbstractValidator;
import org.codehaus.jackson.JsonNode;

public final class PhoneNumberFormatValidator
    extends AbstractValidator
{
    public PhoneNumberFormatValidator(final JsonNode ignored)
    {
        super(ignored);
    }

    @Override
    public boolean validate(final JsonNode node)
    {
        final String input = node.getTextValue();

        final String transformed = input.replaceFirst("^\\((\\d+)\\)", "\\1")
            .replaceFirst("^\\+", "")
            .replaceAll("-(?=\\d)", "")
            .replaceAll(" ", "")
            .replaceAll("\\d", "");

        validationErrors.clear();

        if (transformed.isEmpty())
            return true;

        validationErrors.add("string is not a recognized phone number");
        return false;
    }
}
