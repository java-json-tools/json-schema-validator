package eel.kitchen.jsonschema.validators.factories;

import eel.kitchen.jsonschema.validators.NullValidator;
import eel.kitchen.jsonschema.validators.Validator;
import org.codehaus.jackson.JsonNode;

public final class NullValidatorFactory
    extends AbstractValidatorFactory
{
    public NullValidatorFactory(final JsonNode schemaNode)
    {
        super(schemaNode, NullValidator.class);
    }
}
