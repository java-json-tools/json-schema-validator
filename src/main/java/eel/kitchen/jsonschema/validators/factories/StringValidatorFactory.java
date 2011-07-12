package eel.kitchen.jsonschema.validators.factories;

import eel.kitchen.jsonschema.validators.EnumValidator;
import eel.kitchen.jsonschema.validators.format.FormatValidator;
import eel.kitchen.jsonschema.validators.type.StringValidator;
import org.codehaus.jackson.JsonNode;

public final class StringValidatorFactory
    extends AbstractValidatorFactory
{
    public StringValidatorFactory(final JsonNode schemaNode)
    {
        super(schemaNode, "string", StringValidator.class);

        if (schemaNode.has("format"))
            validatorList.add(FormatValidator.class);
        if (schemaNode.has("enum"))
            validatorList.add(EnumValidator.class);
    }
}
