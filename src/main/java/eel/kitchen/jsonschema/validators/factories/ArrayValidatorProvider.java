package eel.kitchen.jsonschema.validators.factories;

import eel.kitchen.jsonschema.validators.type.ArrayValidator;
import org.codehaus.jackson.JsonNode;

public final class ArrayValidatorProvider
    extends AbstractValidatorProvider
{
    public ArrayValidatorProvider(final JsonNode schemaNode)
    {
        super(schemaNode, "array", ArrayValidator.class);
    }
}
