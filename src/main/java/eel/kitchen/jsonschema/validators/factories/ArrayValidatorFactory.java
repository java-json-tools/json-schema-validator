package eel.kitchen.jsonschema.validators.factories;

import eel.kitchen.jsonschema.validators.type.ArrayValidator;
import org.codehaus.jackson.JsonNode;

public final class ArrayValidatorFactory
    extends AbstractValidatorFactory
{
    public ArrayValidatorFactory(final JsonNode schemaNode)
    {
        super(schemaNode, "array", ArrayValidator.class);
    }
}
