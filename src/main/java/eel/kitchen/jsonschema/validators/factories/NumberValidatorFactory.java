package eel.kitchen.jsonschema.validators.factories;

import eel.kitchen.jsonschema.validators.EnumValidator;
import eel.kitchen.jsonschema.validators.type.NumberValidator;
import org.codehaus.jackson.JsonNode;

public final class NumberValidatorFactory
    extends AbstractValidatorFactory
{
    public NumberValidatorFactory(final JsonNode schemaNode)
    {
        super(schemaNode, "number", NumberValidator.class);

        if (schemaNode.has("enum"))
            validatorList.add(EnumValidator.class);
    }
}
