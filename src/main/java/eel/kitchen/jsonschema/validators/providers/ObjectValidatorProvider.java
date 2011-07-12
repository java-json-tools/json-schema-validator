package eel.kitchen.jsonschema.validators.providers;

import eel.kitchen.jsonschema.validators.type.ObjectValidator;
import org.codehaus.jackson.JsonNode;

public final class ObjectValidatorProvider
    extends AbstractValidatorProvider
{
    public ObjectValidatorProvider(final JsonNode schemaNode)
    {
        super(schemaNode, "object", ObjectValidator.class);
    }
}
