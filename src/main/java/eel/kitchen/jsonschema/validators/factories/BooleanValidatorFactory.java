package eel.kitchen.jsonschema.validators.factories;

import eel.kitchen.jsonschema.validators.BooleanValidator;
import eel.kitchen.jsonschema.validators.EnumValidator;
import org.codehaus.jackson.JsonNode;

public final class BooleanValidatorFactory
    extends AbstractValidatorFactory
{
    public BooleanValidatorFactory(final JsonNode schemaNode)
    {
        super(schemaNode, BooleanValidator.class);

        if (schemaNode.has("enum"))
            validatorList.add(EnumValidator.class);
    }
}
