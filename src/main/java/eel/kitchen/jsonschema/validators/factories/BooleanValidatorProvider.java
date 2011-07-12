package eel.kitchen.jsonschema.validators.factories;

import eel.kitchen.jsonschema.validators.EnumValidator;
import eel.kitchen.jsonschema.validators.type.BooleanValidator;
import org.codehaus.jackson.JsonNode;

public final class BooleanValidatorProvider
    extends AbstractValidatorProvider
{
    public BooleanValidatorProvider(final JsonNode schemaNode)
    {
        super(schemaNode, "boolean", BooleanValidator.class);

        if (schemaNode.has("enum"))
            validatorList.add(EnumValidator.class);
    }
}
