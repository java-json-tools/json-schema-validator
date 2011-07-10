package eel.kitchen.jsonschema.validators.factories;

import eel.kitchen.jsonschema.validators.type.NullValidator;
import org.codehaus.jackson.JsonNode;

public final class NullValidatorFactory
    extends AbstractValidatorFactory
{
    public NullValidatorFactory(final JsonNode schemaNode)
    {
        super(schemaNode, "null", NullValidator.class);
    }
}
