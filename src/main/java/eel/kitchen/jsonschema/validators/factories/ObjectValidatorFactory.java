package eel.kitchen.jsonschema.validators.factories;

import eel.kitchen.jsonschema.validators.type.ObjectValidator;
import org.codehaus.jackson.JsonNode;

public final class ObjectValidatorFactory
    extends AbstractValidatorFactory
{
    public ObjectValidatorFactory(final JsonNode schemaNode)
    {
        super(schemaNode, ObjectValidator.class);
    }
}
