package eel.kitchen.jsonschema.validators.providers;

import eel.kitchen.jsonschema.validators.EnumValidator;
import eel.kitchen.jsonschema.validators.format.FormatValidator;
import eel.kitchen.jsonschema.validators.type.NumberValidator;
import org.codehaus.jackson.JsonNode;

public final class NumberValidatorProvider
    extends AbstractValidatorProvider
{
    public NumberValidatorProvider(final JsonNode schemaNode)
    {
        super(schemaNode, "number", NumberValidator.class);

        if (schemaNode.has("format"))
            validatorList.add(FormatValidator.class);
        if (schemaNode.has("enum"))
            validatorList.add(EnumValidator.class);
    }
}
